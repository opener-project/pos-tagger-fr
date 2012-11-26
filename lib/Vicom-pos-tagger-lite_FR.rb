module Opener
   module Kernel
     module VU
       module POSTagger
    	 module Lite
    	   module FR
      		VERSION = "0.0.1"

      		class Configuration
        		CORE_DIR    = File.expand_path("../core", File.dirname(__FILE__))
        		KERNEL_CORE = CORE_DIR+'/dummy_french_postagger.jar'
      		end

    	  end
    	end
      end
    end
  end
end

KERNEL_CORE=Opener::Kernel::Vicom::POSTagger::Lite::FR::Configuration::KERNEL_CORE
