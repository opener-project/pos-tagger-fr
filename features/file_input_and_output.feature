Feature: Using files as input and output
  In order to postag text
  Using a file as an input
  Using a file as an output

  Scenario Outline: Reverse the text
    Given the fixture file "KafExample.kaf"
    And I put it through the kernel
    Then the output should match the fixture "postagger-output.kaf"
  Examples:
    | input_file         | output_file         |
    | sample_in.txt      | sample_out.txt      |
