# Opener::Kernel::Vicom::POSTagger::Lite::FR

## Initial Version

TODO: Write a gem description

## Installation

Add this line to your application's Gemfile:

    gem 'Vicom-pos-tagger-lite_FR_kernel', :git=>"git@github.com/opener-project/Vicom-pos-tagger-lite_FR_kernel.git"

And then execute:

    $ bundle install

Or install it yourself as:

    $ gem specific_install Vicom-pos-tagger-lite_FR_kernel -l https://github.com/opener-project/Vicom-pos-tagger-lite_FR_kernel.git


If you dont have specifi_install already:

    $ gem install specific_install

## Usage

Once installed as a gem you can access the gem from anywhere:

Vicom-pos-tagger-lite_FR_kernel needs 1 parameters:

1. Tagger model's directory path.
2. You can also specify a 2nd parameter to use static timestamp at KAF header: -n.

For example:

$ Vicom-pos-tagger-lite_FR_kernel ./ french.txt

01. `<KAF version="v1.opener" xml:lang="fr">`
02. `  <kafHeader>`
03. `    <fileDesc filename="french" filetype="TXT"/>`
04. `    <linguisticProcessors layer="text">`
05. `      <lp name="openlp-fr-tok" timestamp="2013-02-22T12:59:16Z" version="1.0"/>`
06. `    </linguisticProcessors>`
07. `    <linguisticProcessors layer="terms">`
08. `      <lp name="opennlp-pos-treetagger-fr" timestamp="2013-02-11T11:07:17Z" version="1.0"/>`
09. `      <lp name="opennlp-multiword-fr" timestamp="2013-02-11T11:07:17Z" version="1.0"/>`
10. `    </linguisticProcessors>`
11. `  </kafHeader>`
12. `  <text></text>`
13. `  <terms>`
14. `    <term lemma="ancien" pos="G" tid="t1" type="open">`
15. `      <span>`
16. `        <!--Ancien-->`
17. `        <target id="w1"/>`
18. `      </span>`
19. `    </term>`
20. `    <term lemma="chef" pos="N" tid="t2" type="open">`
21. `      <span>`
22. `        <!--chef-->`
23. `        <target id="w2"/>`
24. `      </span>`
25. `    </term>`
26. `    <term lemma="charismatique" pos="G" tid="t3" type="open">`
27. `      <span>`
28. `        <!--charismatique-->`
29. `        <target id="w3"/>`
30. `      </span>`
31. `    </term>`
32. `    <term lemma="d'" pos="P" tid="t4" type="close">`
33. `      <span>`
34. `        <!--d'-->`
35. `        <target id="w4"/>`
36. `      </span>`
37. `    </term>`
38. `  </terms>`
39. `</KAF>`


Will output:

## Contributing

1. Pull it
2. Create your feature branch (`git checkout -b features/my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin features/my-new-feature`)
5. If you're confident, merge your changes into master.
