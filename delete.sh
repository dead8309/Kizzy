#!/bin/sh

#
# /*
# * ******************************************************************
#  *  * Copyright (C) 2022
#  *  * delete.sh is part of Kizzy
#  *  *  and can not be copied and/or distributed without the express
#  *  * permission of yzziK(Vaibhav)
#  *  *****************************************************************
#  */
#
#

# Remove consumer-rules.pro file from all directories and subdirectories except for app
find . -type f -name "consumer-rules.pro" ! -path "./app/*" -print -delete

# Remove consumerProguardFiles("consumer-rules.pro") from all build.gradle.kts files except for those in app
find . -type f -name "build.gradle.kts" ! -path "./app/*" -print -exec sed -i '/consumerProguardFiles("consumer-rules.pro")/d' {} \;
