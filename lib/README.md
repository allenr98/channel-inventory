# Why are there jar files here?
In a word: expediency.  I was having trouble getting consistent access to the maven repositories
that hold the jar files.  It was all good from my home workstation, but as I moved around to my
workplace or various other places, I was running into problems resolving dependencies. So I could
get down to the business of actually writing code, I just downloaded the latest versions of the
jars and checked them in with the rest of my code. I have high hopes that at some point in the
not too distant future I'll be able to remove them and just reference a URL out in the interwebz
somewhere.