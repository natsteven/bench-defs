# This file is part of the competition environment.
#
# SPDX-FileCopyrightText: 2011-2020 Dirk Beyer <https://www.sosy-lab.org>
#
# SPDX-License-Identifier: Apache-2.0

GIT_REPOS := archives sv-benchmarks benchexec scripts coveriteam

init: $(GIT_REPOS)

# Always try to update git repos - without PHONY, make wouldn't update once the directories exist
.PHONY: $(GIT_REPOS)
$(GIT_REPOS):
	git submodule update --init $@

.PHONY: update
update: | update-repos
	git pull --rebase

update-repos: $(foreach g,$(GIT_REPOS),$(g)/.update)
$(foreach g,$(GIT_REPOS),$(g)/.update): $(GIT_REPOS)
	@echo \# Updating $(@D)
	cd $(@D) && \
		git checkout master || git checkout trunk && \
		git pull --rebase || true

bin/%:	./archives/2021/%.zip
	./scripts/mkInstall.sh $(*F)

