//
//  DataManagement.hpp
//  GeoServices
//
//  Created by josh mcloskey on 9/24/15.
//  Copyright © 2015 Josh . All rights reserved.
//

#ifndef DataManagement_hpp
#define DataManagement_hpp

#include <stdio.h>
#include <vector>
#include <stack>
#include <map>
#include <string>
#include <cassert>
const int MAX_NAME_SIZE = 32;
class Message{
    
};
class Name{
    Name(const char * name, int size){
        assert(size<=MAX_NAME_SIZE);
        std::strcpy(this->name, name);
    }
    const char * getName(){
        return &this->name[0];
    }
    
private:
    int length;
    char name[MAX_NAME_SIZE]; // You can only have a name of length 32
};
class DataStructure{
    
};

class DataManagement{
private:
    std::stack<DataStructure *> myStack;
    
};

#endif /* DataManagement_hpp */
