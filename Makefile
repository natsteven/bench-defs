# This file is part of the competition environment.
#
# SPDX-FileCopyrightText: 2011-2020 Dirk Beyer <https://www.sosy-lab.org>
#
# SPDX-License-Identifier: Apache-2.0

GIT_REPOS := archives sv-benchmarks benchexec scripts coveriteam

init: $(GIT_REPOS)

$(GIT_REPOS):
	git submodule update --init $@

update: | update-repos
	@echo "\n# Updating" bench-defs
	git pull --rebase

update-repos: $(foreach g,$(GIT_REPOS),$(g)/.update)

$(foreach g,$(GIT_REPOS),$(g)/.update): $(GIT_REPOS)
	@echo "\n# Updating" $(@D)
	cd $(@D) && \
		git checkout master || git checkout trunk && \
		git pull --rebase || true

bin/%:	./archives/2021/%.zip
	./scripts/execute-runs/mkInstall.sh $(*F)

