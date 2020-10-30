#!/usr/bin/env python3
import argparse
import sys
import os
import logging
from pathlib import Path
from urllib import request
from lxml import etree
import yaml

BENCHDEF_SUFFIX = ".xml"
ALLOWLIST_TASK_SETS = []

COLOR_RED = "\033[31;1m"
COLOR_GREEN = "\033[32;1m"
COLOR_ORANGE = "\033[33;1m"
COLOR_MAGENTA = "\033[35;1m"

COLOR_DEFAULT = "\033[m"
COLOR_DESCRIPTION = COLOR_MAGENTA
COLOR_VALUE = COLOR_GREEN
COLOR_WARNING = COLOR_RED

# if not sys.stdout.isatty():
#    COLOR_DEFAULT = ''
#    COLOR_DESCRIPTION = ''
#    COLOR_VALUE = ''
#    COLOR_WARNING = ''


def addColor(description, value, color=COLOR_VALUE, sep=": "):
    return "".join(
        (
            COLOR_DESCRIPTION,
            description,
            COLOR_DEFAULT,
            sep,
            color,
            value,
            COLOR_DEFAULT,
        )
    )


def error(msg, cause=None, label="    ERROR"):
    msg = addColor(label, str(msg), color=COLOR_WARNING)
    if cause:
        logging.exception(msg)
    else:
        logging.error(msg)
    global errorFound


def info(msg, label="INFO"):
    msg = str(msg)
    if label:
        msg = addColor(label, msg)
    logging.info(msg)


class DTDResolver(etree.Resolver):
    def __init__(self):
        self.cache = dict()

    def resolve(self, url, d_id, context):
        if url.startswith("http"):
            if url in self.cache:
                dtd_content = self.cache[url]
            else:
                dtd_content = self._download(url)
                self.cache[url] = dtd_content
            return self.resolve_string(dtd_content, context)
        return super().resolve(url, d_id, context)

    def _download(self, url):
        with request.urlopen(url) as inp:
            dtd_content = inp.read()
        return dtd_content


PARSER = etree.XMLParser(dtd_validation=True)
"""XML parser used in unit tests."""
PARSER.resolvers.add(DTDResolver())


def _check_valid(xml_file: Path):
    """Tries to parse the given xml file and returns any produced exceptions."""
    try:
        etree.parse(str(xml_file), PARSER)
        return []
    except etree.ParseError as e:
        return [e]
    except etree.XMLSyntaxError as e:
        return [e]


def _get_tasks(xml_file):
    xml_root = etree.parse(str(xml_file))
    return list(xml_root.iter(tag="tasks"))


def _check_task_defs_match_set(xml_file: Path, /, tasks_dir: Path):
    """Checks that each task element in the given xml_file fulfills certain criteria.

    The criteria are the following:
    1. each task element has an attribute 'name'
    2. each task element contains exactly one includesfile element
    3. the includesfile element references a `.set`-file in the sv-benchmarks directory
    4. the referenced `.set`-file matches the name of the task element
    """
    errors = []
    for task_tag in _get_tasks(xml_file):
        name = task_tag.get("name")
        if not name:
            errors.append(
                "Task tag is missing name in line {}".format(task_tag.sourceline)
            )

        includes = task_tag.findall("includesfile")
        if len(includes) != 1:
            errors.append(
                "Expected exactly one <includesfile> tag for tasks {}".format(name)
            )
        else:
            include = includes[0]
            included_set = Path(include.text)
            benchmark_dir = (xml_file.parent / included_set.parent).resolve()
            expected_dir = tasks_dir.resolve()
            if benchmark_dir != expected_dir:
                errors.append(
                    "Expected benchmark directory to be {} for tasks {} (was {})".format(
                        expected_dir, name, benchmark_dir
                    )
                )
            set_file = included_set.name
            if not set_file.endswith(".set"):
                errors.append("Set name does not end on '.set': {}".format(set_file))

            set_name = ".".join(set_file.split(".")[:-1])
            if not set_name == name:
                errors.append(
                    "Set name not consistent with tasks name: {} vs. {}".format(
                        set_name, name
                    )
                )

    return errors


def _check_bench_def(xml: Path, /, tasks_dir: Path):
    """Checks the given xml benchmark definition for conformance."""
    info(str(xml), label="CHECKING")
    errors = _check_valid(xml)
    errors += _check_task_defs_match_set(xml, tasks_dir=tasks_dir)
    if errors:
        error(xml)
        for msg in errors:
            error(msg)
    return bool(errors)


def parse_yaml(yaml_file):
    with open(yaml_file) as inp:
        return yaml.safe_load(inp)


def parse_args(argv):
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--category-structure",
        default="benchmark-defs/category-structure.yml",
        required=False,
        help="category-structure.yml to use",
    )
    parser.add_argument(
        "--tasks-directory",
        default="sv-benchmarks/c",
        required=False,
        help="directory to benchmark tasks",
    )
    parser.add_argument(
        "benchmark_definition",
        nargs="+",
        help="benchmark-definition XML files to check",
    )

    args = parser.parse_args(argv)

    args.category_structure = Path(args.category_structure)
    args.tasks_directory = Path(args.tasks_directory)
    args.benchmark_definition = [
        Path(bench_def) for bench_def in args.benchmark_definition
    ]

    missing_files = [
        f
        for f in args.benchmark_definition + [args.category_structure]
        if not f.exists()
    ]
    if missing_files:
        raise ValueError(
            f"File(s) do not exist: {','.join([str(f) for f in missing_files])}"
        )
    return args


def _check_all_sets_used(bench_def: Path, /, exceptions: list):
    return True


def _verifiers_in_overall(category_info):
    return category_info["categories"]["Overall"]["verifiers"]


def main(argv=None):
    if argv is None:
        argv = sys.argv[1:]
    args = parse_args(argv)

    success = True
    for bench_def in args.benchmark_definition:
        success &= _check_bench_def(bench_def, tasks_dir=args.tasks_directory)

    category_info = parse_yaml(args.category_structure)
    verifiers_in_overall = _verifiers_in_overall(category_info)
    for verifier in verifiers_in_overall:
        bench_def = verifier + BENCHDEF_SUFFIX
        success &= _check_all_sets_used(bench_def, exceptions=ALLOWLIST_TASK_SETS)

    return 0 if success else 1


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format=None)
    sys.exit(main())
