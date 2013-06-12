# Opener::POSTaggers::FR

## Initial Version

This module uses (internally) Apache OpenNLP programatically to perform POS tagging.
The OpenNLP pos-model has been trained using the French TreeBank corpus.

Using a 75% of the corpus as training set and the remaining 25% as test, the OpenNLP self evaluation tool gives a 91,5% of precission.

## Installation

Branch: refactoring

To install this gem you need to have installed: Java 6, Apache Maven3, Ruby, RubyGems and of course git itself.

Clone the repo

    git clone git@github.com/opener-project/pos-tagger-fr.git

Then go to the core directory inside the repo, you will see a src folder, and a pom.xml (which tells to Maven how to build all the java stuff)
Execute:

	mvn clean package

This will compile the java source code, download all the dependencies, and create a single selfcontained uber jar inside a "target" folder.

Then you can go back to the root of the repository (where the .gemspec file is located) and issue

	gem build opener-pos-tagger-fr.gemspec

If no error happens, then you can install the gem

	gem install opener-pos-tagger-fr

Note: to install a gem to a system location you will probably need sudo permissions

## Usage

Once installed as a gem you can access the gem from anywhere, as a regular shell command.
The command reads the standard input, so you have to pipe the content to it.

    $ cat kaf_file.kaf | pos-tagger-fr



For example:

$ cat fremch.kaf | pos-tagger-fr


## Issues to fix

The gem is not admitting a parameter to set a static timestamp in KAF header.
It does internally for cucumber test, but not when the gem is called from the command line.

Many things have to be revised. In the gemspec file, the gem-files included are picked issuing a 'git ls-files' command.
This is does not work if a non-git-tracked files is going to be used inside the gem, like the maven generated jar file.
This jar artifact should not be tracked by git, as it is a binary artifact generated from the source (and it weights many MB)
For now it has been fixed adding a push(PATH_TO_JAR_FILE) to that field, but it is a little bit dirty workaround, since it needs the actual jar file name hardcoded.
Similar issue arises inside lib/Vicom-pos-tagger-lite_FR_kernel.rb which requires the exact name of the jar to create the "@kernel" variable.
This means that in case the jar name changes, those files have to be manually adapted (bad thing...).


## Contributing

1. Pull it
2. Create your feature branch (`git checkout -b features/my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin features/my-new-feature`)
5. If you're confident, merge your changes into master.
