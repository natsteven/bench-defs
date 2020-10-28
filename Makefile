GIT_REPOS := benchexec sv-benchmarks archives

init: $(GIT_REPOS)

# Always try to update git repos - without PHONY, make wouldn't update once the directories exist
.PHONY: $(GIT_REPOS)
$(GIT_REPOS):
	git submodule update --init $@

update: | update-repos provenance.txt

update-repos: $(foreach g,$(GIT_REPOS),$(g)/.update)
$(foreach g,$(GIT_REPOS),$(g)/.update): $(GIT_REPOS)
	@echo \# Updating $(@D)
	cd $(@D) && \
		git checkout master || git checkout trunk && \
		git pull --rebase || true

.PHONY: provenance.txt
provenance.txt:
	./scripts/mkProvenanceInfo.sh $(GIT_REPOS)

bin/%:	./archives/2021/%.zip
	./scripts/mkInstall.sh $(*F)

