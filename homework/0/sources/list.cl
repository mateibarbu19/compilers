class List inherits IO {
    first : Object;
    next : List;

    first() : Object { first };
    next() : List { next };

    set_first(o : Object) : SELF_TYPE {{
        first <- o;

        self;
    }};

    set_next(n : List) : SELF_TYPE {{
        next <- n;

        self;
    }};

    is_empty() : Bool { isvoid first };
    is_last() : Bool { isvoid next };

    to_list(object : Object) : List {
        case
            object
        of
            l : List => l;
            o : Object => new List;
        esac
    };

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
            index <= 1
        then
            self
        else
            if
                isvoid next
            then
                new List
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

    delete_nth_elem(index : Int) : Object {
        let
            removed : Object,
            prev : List <- get_from_nth(index - 1)
        in {
            if
                not prev.is_empty()
            then
                if
                    not isvoid prev.next()
                then {
                    removed <- prev.next().first();
                    prev.set_next(prev.next().next());
                } else {
                    removed <- prev.first();
                    prev.set_first(while false loop false pool);
                } fi
            else
                false
            fi;

            removed;
        }
    };

    -- because there is no to_string function for basic types
    first_to_string() : String {
        if
            not isvoid first
        then
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
        else
            ""
        fi
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

    merge(other : List) : SELF_TYPE {{
        get_last().set_next(other);

        self;
    }};

    filter(f : Filter) : SELF_TYPE {
        let
            res : List <- new List,
            l : List <- self
        in {
            while
                not isvoid l
            loop {
                if
                    f.apply(l.first())
                then
                    res.add(l.first())
                else
                    false
                fi;

                l <- l.next();
            } pool;

            set_first(res.first()).set_next(res.next());
        }
    };

    sortBy() : SELF_TYPE {
        self (* TODO *)
    };
};
