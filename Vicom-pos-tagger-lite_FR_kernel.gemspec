# -*- encoding: utf-8 -*-
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)

require 'Vicom-pos-tagger-lite_FR_kernel'

Gem::Specification.new do |gem|
  gem.name          = "Vicom-pos-tagger-lite_FR_kernel"
  gem.version       = Opener::Kernel::Vicom::POSTagger::Lite::FR::VERSION
  gem.authors       = ["sb-olr","sparkboxx"]
  gem.email         = ["sujit@olery.com", "wilco@olery.com"]
  gem.description   = %q{Part of Speech tagger for french lite version }
  gem.summary       = %q{Use this gem in a component}
  gem.homepage      = "http://opener-project.github.com/"

  gem.files         = `git ls-files`.split($/).push('core/target/Vicom-postagger_FR-0.9.jar')
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})
  gem.require_paths = ["lib"]
  gem.bindir        = 'bin'

  gem.add_development_dependency 'rspec'
  gem.add_development_dependency 'cucumber'
  gem.add_development_dependency 'pry'

end
