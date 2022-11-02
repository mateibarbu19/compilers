class Main inherits IO {
    lists : List <- new List;
    working_list : List;
    strtok : StrTok <- new StrTok;
    loading : Bool <- true;
    in_strs : InputStrings <- new InputStrings;
    out_strs : OutputStrings <- new OutputStrings;
    atoi : A2I <- new A2I;
    helper : Helper <- new Helper;

    which() : Object {
        let
            token : String <- strtok.get()
        in
            if
                token = new Object.type_name()
            then
                new Object
            else
                if
                    token = new IO.type_name()
                then
                    new IO
                else
                    if 
                        token = new Int.type_name()
                    then
                        atoi.a2i(strtok.get())
                    else
                        if
                            token = new Bool.type_name()
                        then
                            if 
                                strtok.get() = in_strs.true_str()
                            then
                                true
                            else
                                false
                            fi
                        else
                            if
                                token = new String.type_name()
                            then
                                strtok.get()
                            else
                                if
                                    helper.describes_product(token)
                                then
                                    helper.build_product(token, strtok.get(), strtok.get(), atoi.a2i(strtok.get()))
                                else
                                    if
                                        helper.describes_rank(token)
                                    then
                                        helper.build_rank(token, strtok.get())
                                    else
                                        new Object
                                    fi
                                fi
                            fi
                        fi
                    fi
                fi
            fi
    };

    load(line : String) : Object {{
        working_list <- new List;

        while
            loading
        loop
            if
                line = in_strs.end_str()
            then {
                loading <- false;
                lists.add(working_list);
            } else {
                strtok.init(line, in_strs.delimiter());
                working_list.add(which());

                line <- in_string();
            } fi
        pool;

        working_list <- new List;
    }};

    print(line : String) : Object {
        let
            s : String <- strtok.get(),
            index : Int <- atoi.a2i(s),
            l : List <- lists
        in
            if
                index = 0
            then
                while
                    not isvoid l
                loop {
                    index <- index + 1;
                    out_string(atoi.i2a(index));
                    out_string(out_strs.list_delimiter());
                    out_string(l.first_to_string());

                    l <- l.next();
                } pool
            else
                out_string(
                    lists.
                        get_from_nth(index).
                            first_to_string()
                )
            fi
    };

    main() : Object {{
        let
            line : String,
            token : String,
            looping : Bool <- true
        in    
            while
                looping 
            loop {
                line <- in_string();
                strtok.init(line, in_strs.delimiter());
                token <- strtok.get();

                if
                    line = in_strs.cmd_help()
                then
                    out_string(out_strs.help_banner())
                else
                    if
                        loading
                    then
                        load(line)
                    else
                        if
                            line = in_strs.cmd_load()
                        then
                            loading <- true
                        else
                            if
                                token = in_strs.cmd_print()
                            then
                                print(line)
                            else
                                looping <- false
                            fi
                        fi
                    fi
                fi;
            } pool;
    
        self;
    }};
};
