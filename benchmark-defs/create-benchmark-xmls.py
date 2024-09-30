#!/usr/bin/env python3

import argparse
import os
import sys
import yaml

from xml.etree import ElementTree as ET

RELEVANT_COMPETITION = "SV-COMP 2024"
FM_TOOLS_BENCHEXEC_TOOLINFO_MODULE = "benchexec_toolinfo_module"
FM_TOOLS_INPUT_LANGUAGE = "input_languages"
FM_TOOLS_PARTICIPATION = "competition_participations"
XML_DOCTYPE_DECLARATION = """<?xml version="1.0"?>
<!DOCTYPE benchmark PUBLIC "+//IDN sosy-lab.org//DTD BenchExec benchmark 1.9//EN" "https://www.sosy-lab.org/benchexec/benchmark-2.3.dtd">
"""


# This class is copied and adjusted from
# https://stackoverflow.com/a/34324359/3012884
# We could also use lxml instead of the stdlib's ElementTree, but this would
# introduce an additional dependency.
class CommentedTreeBuilder(ET.TreeBuilder):
    def comment(self, data):
        self.start(ET.Comment, {})
        self.data(data)
        self.end(ET.Comment)


def parse_xml_with_comments(xml_string):
    parser = ET.XMLParser(target=CommentedTreeBuilder())
    return ET.fromstring(xml_string, parser=parser)


def participates_as_c_verifier(tool_file):
    try:
        tool_data = yaml.safe_load(open(tool_file))
        return "C" in tool_data[FM_TOOLS_INPUT_LANGUAGE] and any(
            p["competition"] == RELEVANT_COMPETITION and p["track"] == "Verification"
            for p in tool_data[FM_TOOLS_PARTICIPATION]
        )
    except:
        return False


def get_c_verifiers(data_dir: str) -> dict:
    return {
        get_tool_name(fil): os.path.join(data_dir, fil)
        for fil in os.listdir(data_dir)
        if participates_as_c_verifier(os.path.join(data_dir, fil))
    }


def get_tool_name(filename: str) -> str:
    return filename.split(".yml")[0].split("/")[-1]


def parse_cli(argv):
    parser = argparse.ArgumentParser(
        description="Create a benchmark XML for a fm-tools data file"
    )
    parser.add_argument(
        "--fm-data", required=True, help="fm-tools data file or directory"
    )
    parser.add_argument(
        "--xml-template", required=True, help="XML template file to use"
    )
    parser.add_argument(
        "--category-structure", required=True, help="Category structure to use"
    )
    parser.add_argument("--output", required=True, help="Output folder")
    args = parser.parse_args(argv)

    if not os.path.exists(args.fm_data):
        raise ValueError(f"File {args.fm_data} does not exist")
    if os.path.isdir(args.fm_data):
        args.fm_data = get_c_verifiers(args.fm_data)
    else:
        tool_name = get_tool_name(args.fm_data)
        args.fm_data = {tool_name: args.fm_data}
    args.fm_data = {
        tool_name: yaml.safe_load(open(data_file))
        for tool_name, data_file in args.fm_data.items()
    }

    if not os.path.exists(args.xml_template):
        raise ValueError(f"File {args.xml_template} does not exist")
    args.xml_template = open(args.xml_template).read()

    if not os.path.exists(args.category_structure):
        raise ValueError(f"File {args.category_structure} does not exist")
    args.category_structure = yaml.safe_load(open(args.category_structure))

    if not os.path.exists(args.output) or not os.path.isdir(args.output):
        raise ValueError(
            f"Directory {args.output} does not exist or is not a directory"
        )

    return args


def _get_toolinfo_name(data: dict) -> str:
    module = data[FM_TOOLS_BENCHEXEC_TOOLINFO_MODULE]
    if module.startswith("benchexec.tools."):
        return module[len("benchexec.tools.") :]
    if module.startswith("http") and module.endswith(".py"):
        return module.split("/")[-1].split(".py")[0]
    return module


def get_category_name_as_in_xml(category_name_as_in_category_structure: str) -> str:
    try:
        return category_name_as_in_category_structure.split(".")[1]
    except IndexError:
        raise ValueError(
            f"Unexpected category name: {category_name_as_in_category_structure}"
        )


def purge_categories(xml_str, tool_name, category_structure) -> str:
    categories_tool_participates_in = set()
    cs = category_structure
    for metaname, metacategory in cs["categories"].items():
        if tool_name in metacategory["verifiers"]:
            subcategories = metacategory["categories"]
            try:
                category_names_as_in_xml = {
                    get_category_name_as_in_xml(category) for category in subcategories
                }
            except ValueError:
                print(
                    f"Ignoring {metaname} because of unexpected subcategory",
                    file=sys.stderr,
                )
            else:
                categories_tool_participates_in |= category_names_as_in_xml
    optins = cs["opt_in"].get(tool_name, [])
    category_names_as_in_xml = {
        get_category_name_as_in_xml(category) for category in optins
    }
    categories_tool_participates_in |= category_names_as_in_xml

    root = parse_xml_with_comments(xml_str)
    # The below XML modification expects the following template xml structure:
    # <benchmark [...]>
    #   <rundefinition name="rundef1">
    #     <tasks name="task1">[...]</tasks>
    #     [...]
    #     <tasks name="taskn">[...]</tasks>
    #   </rundefinition>
    #   [...]
    #   <rundefinition name="rundefm">
    #   [...]
    for rundef in root.findall("rundefinition"):
        tasks = rundef.findall("tasks")
        for taskdef in tasks:
            if taskdef.get("name") not in categories_tool_participates_in:
                rundef.remove(taskdef)
        leftover_tasks = rundef.findall("tasks")
        if not leftover_tasks:
            root.remove(rundef)
    new_xml = ET.tostring(root, encoding="unicode")
    return XML_DOCTYPE_DECLARATION + new_xml


def handle_verifier_data(tool_name, data, cli_args):
    display_name = data["name"]
    toolinfo_name = _get_toolinfo_name(data)
    xml_with_all_categories = cli_args.xml_template.format(
        toolinfo_name=toolinfo_name, name=display_name
    )

    xml_with_only_opt_ins = purge_categories(
        xml_with_all_categories, tool_name, cli_args.category_structure
    )

    output_file = os.path.join(cli_args.output, f"{tool_name}.xml")
    with open(output_file, "w") as out:
        out.write(xml_with_only_opt_ins)


def main(argv=None):
    if argv is None:
        argv = sys.argv[1:]
    cli_args = parse_cli(argv)
    data = cli_args.fm_data

    for name, single_verifier in data.items():
        handle_verifier_data(name, single_verifier, cli_args)


if __name__ == "__main__":
    sys.exit(main())
