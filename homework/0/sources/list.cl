class List inherits IO {
    first : Object;
    next : List;

    first() : Object { first };
    next() : List { next };

    set_next(n : List) : SELF_TYPE {{
        next <- n;

        self;
    }};

    is_empty() : Bool { isvoid first };
    is_last() : Bool { isvoid next };

    get_last() : List {
        if
            isvoid next
        then 
            self
        else
            next.get_last()
        fi
    };

    get_from_nth(index : Int) : List {
        if
            index = 1
        then
            self
        else
            if
                isvoid next
            then
                self
            else
                next.get_from_nth(index - 1)
            fi
        fi  
    };

    add(o : Object) : List {{
        let
            last : List <- get_last()
        in
            if
                is_empty()
            then
                first <- o
            else
                last.set_next(new List.add(o))
            fi;

        self;
    }};

    -- because there is no to_string function for basic types
    first_to_string() : String {
        case
            first
        of
            l : List => l.to_string();
            o : Object => o.type_name().concat("()");
            io : IO => io.type_name().concat("()");
            i : Int =>
                i.type_name().concat("(").concat(new A2I.i2a(i)).concat(")");
            s : String =>
                s.type_name().concat("(").concat(s).concat(")");
            b : Bool =>
                if
                    b
                then
                    b.type_name().concat("(true)")
                else
                    b.type_name().concat("(false)")
                fi;
            p : Product => p.to_string();
            r : Rank => r.to_string();
        esac
    };

    to_string() : String {
        let
            res : String <- "[ ",
            l : List <- self
        in {
            while
                not isvoid l
            loop {
                res <- res.concat(l.first_to_string());
                res <- res.concat(
                    if
                        l.is_last()
                    then
                        ""
                    else
                        ", "
                    fi
                );

                l <- l.next();
            } pool;

            res <- res.concat(" ]\n");
        }
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
