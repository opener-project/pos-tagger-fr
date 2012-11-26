module Opener
  module Sample
    module Kernel
      VERSION = "0.0.2"

      class Configuration
        CORE_DIR    = File.expand_path("../core", File.dirname(__FILE__))
        KERNEL_CORE = "java -jar "+CORE_DIR+'/dummy_french_postagger.jar'
      end

    end
  end
end

KERNEL_CORE=Opener::Sample::Kernel::Configuration::KERNEL_CORE
