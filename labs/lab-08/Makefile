UTILS=../utils

ifndef CONFIG
	CONFIG=config_output.yaml
endif

ifndef HEAD
	HEAD=$(UTILS)/head.tex
endif

build: README.pdf

README.pdf README.tex: README.md header.yaml $(CONFIG) $(HEAD)
	pandoc -d $(CONFIG) \
		-M date="`date "+%d %B %Y"`" \
		--include-in-header $(HEAD) \
		-o $@

README.html: README.md header.yaml $(CONFIG) $(HEAD)
	pandoc -d $(CONFIG) \
		-M date="`date "+%d %B %Y"`" \
		-o $@

clean:
	rm -f README.pdf README.tex README.html
