require 'open3'
require_relative 'fr/version'

module Opener
  module POSTaggers
    ##
    # The POS tagger that supports French.
    #
    # @!attribute [r] args
    #  @return [Array]
    # @!attribute [r] options
    #  @return [Hash]
    #
    class FR
      attr_reader :args, :options

      ##
      # @param [Hash] options
      #
      # @option options [Array] :args The commandline arguments to pass to the
      #  underlying Python script.
      #
      def initialize(options = {})
        @args          = options.delete(:args) || []
        @options       = options
      end

      ##
      # Builds the command used to execute the kernel.
      #
      # @return [String]
      #
      def command
        "java -jar #{kernel} -l #{lang} #{args.join(' ')}"
      end

      ##
      # Runs the command and returns the output of STDOUT, STDERR and the
      # process information.
      #
      # @param [String] input The input to tag.
      # @return [Array]
      #
      def run(input)
        return Open3.capture3(command, :stdin_data => input)
      end

      protected

      ##
      # @return [String]
      #
      def core_dir
        File.expand_path("../../../core", File.dirname(__FILE__))
      end  

      ##
      # @return [String]
      #
      def kernel
        core_dir+'/target/Vicom-postagger_FR-0.9.jar'
      end 
      
      def lang
        'fr'
      end
      
    end # FR
  end # POSTaggers
end # Opener











