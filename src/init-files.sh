#!/bin/bash
awk -v q='"' 'BEGIN{
    sedCmd="gsed"
    for (d=1; d<=25; d++){
        d2 = d<10? "0"d : d

        printf "printf %sInitializing the source and inputs for Day%s ... %s\n", q, d2, q
        # clone kt files: Day0x.kt
        printf "cp template.kt Day%s.kt\n", d2

        # clone input files: inputs/Day0x.txt and inputs/Day0x-test.txt
        printf "touch inputs/Day%s.txt; touch inputs/Day%s-test.txt\n", d2, d2

        #fix the day numbers in kt files. template has '1x'
        printf "%s -i %s1 s#day/1x#day/%s#%s Day%s.kt\n", sedCmd, q, d, q, d2
        printf "%s -i %ss#Day1x#Day%s#%s Day%s.kt\n", sedCmd, q, d2, q, d2

        printf "echo %sDone%s\n", q, q
    }
}'

# pipe to "sh" if output looks ok
