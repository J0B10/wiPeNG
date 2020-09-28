# wiPeNG

wiPeNG is a command line snippet that cleans up all PNG files inside a directory that are completely empty (esp. have an alpha value of 0 for all pixels).
It is optimized for checking and removing thousands of images by utilizing multithreading and an efficient algorithm.

It was initially created to reduce file sizes of community packages for Microsoft Flight Simulator by removing all empty aerial images.

## Parameters
* `--threads`/ `-t` amount of threads to use for execution, change depending on how many threads your cpu has for max speed, default is 2  
* `--dir`/`-d` directory where to look for the png files, default is your current working directory
