DIR = $(shell basename `pwd`)
DOCDIR = docs

all: 
	javac *.java

clean:
	/bin/rm -f *.class *~ 
	/bin/rm -fr docs

README.html: README
	echo '<html><head><title>README</title><body><pre>' > $@
	cat README >> $@
	echo '</pre></body></html>' >> $@

docs: README.html
	mkdir $(DOCDIR)
	javadoc -d $(DOCDIR) -private -overview README.html -windowtitle "Hack-a-Vote" -author *.java

dist: clean docs
	/bin/rm -f $(DIR).tar.gz
	cd .. && tar -cWv --exclude CVS -f $(DIR).tar $(DIR)
	/bin/mv ../$(DIR).tar .
	gzip -v9 $(DIR).tar
