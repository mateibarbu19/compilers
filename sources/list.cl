class List inherits IO {
    first : Object;
    next : List;

    get_last() : List {
        if 
            isvoid
        next
        then 
            self
        else
            next.get_last()
        fi
    };

    add(o : Object) : SELF_TYPE {{
        if
            get_last() = self
        then
            first <- o
        else
            next <- new List.add(o)
        fi;

        self;
    }};

    toString() : String {
        "[TODO: implement me]"
    };

    merge(other : List) : SELF_TYPE {
        self (* TODO *)
    };

    filterBy() : SELF_TYPE {
        self (* TODO *)
    };

    sortBy() : SELF_TYPE {
        self (* TODO *)
    };
};
