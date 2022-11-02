class Main inherits IO {
    lists : List <- new List;
    working_list : List;
    strtok : StrTok <- new StrTok;
    loading : Bool <- true;
    in_strs : InputStrings <- new InputStrings;
    out_strs : OutputStrings <- new OutputStrings;
    atoi : A2I <- new A2I;
    helper : Helper <- new Helper;

    which_element(type : String) : Object {
        if
            type = new Object.type_name()
        then
            new Object
        else
            if
                type = new IO.type_name()
            then
                new IO
            else
                if 
                    type = new Int.type_name()
                then
                    atoi.a2i(strtok.get())
                else
                    if
                        type = new Bool.type_name()
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
                            type = new String.type_name()
                        then
                            strtok.get()
                        else
                            if
                                helper.describes_product(type)
                            then
                                helper.build_product(type, strtok.get(), strtok.get(), atoi.a2i(strtok.get()))
                            else
                                if
                                    helper.describes_rank(type)
                                then
                                    helper.build_rank(type, strtok.get())
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

    which_filter(type : String) : Filter {
        if
            type = new ProductFilter.type_name()
        then
            new ProductFilter
        else
            if
                type = new RankFilter.type_name()
            then
                new RankFilter
            else
                if
                    type = new SamePriceFilter.type_name()
                then
                    new SamePriceFilter
                else
                    new Filter
                fi
            fi
        fi
    };

    which_comparator(type : String) : Comparator {
        if
            type = new PriceComparator.type_name()
        then
            new PriceComparator
        else
            if
                type = new RankComparator.type_name()
            then
                new RankComparator
            else
                if
                    type = new AlphabeticComparator.type_name()
                then
                    new AlphabeticComparator
                else
                    new Comparator
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
                working_list.add(which_element(strtok.get()));

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
                out_string(lists.get_from_nth(index).first_to_string())
            fi
    };

    merge(pos1 : String, pos2 : String) : List {
        let
            index1 : Int <- atoi.a2i(pos1),
            index2 : Int <- atoi.a2i(pos2),
            
            l1 : List,
            l2 : List
        in {
            if
                index1 < index2
            then {
                l2 <- lists.to_list(
                        lists.delete_nth_elem(index2));
                l1 <- lists.to_list(
                    lists.delete_nth_elem(index1));
            } else {
                l1 <- lists.to_list(
                        lists.delete_nth_elem(index1));
                l2 <- lists.to_list(
                    lists.delete_nth_elem(index2));
            } fi;

            lists.add(l1.merge(l2));
        }
    };

    filter(pos : String, type : String) : Object {
        let
            index : Int <- atoi.a2i(pos),
            f : Filter <- which_filter(type),
            l : List <- lists.to_list(
                            lists.get_from_nth(index).first()
                        ).filter(f)
        in
            l
    };

    sort(pos : String, type : String, way : String) : Object {
        let
            index : Int <- atoi.a2i(pos),
            c : Comparator <- which_comparator(type),
            l : List <- lists.to_list(
                            lists.get_from_nth(index).first()
                        ).sort(c)
        in
            if
                way = in_strs.ascendent()
            then
                l
            else
                l.reverse()
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
                                if
                                    token = in_strs.cmd_merge()
                                then
                                    merge(strtok.get(), strtok.get())
                                else
                                    if
                                        token = in_strs.cmd_filterby()
                                    then
                                        filter(strtok.get(), strtok.get())
                                    else
                                        if
                                            token = in_strs.cmd_sortby()
                                        then
                                            sort(strtok.get(), strtok.get(), strtok.get())
                                        else
                                            looping <- false
                                        fi
                                    fi
                                fi
                            fi
                        fi
                    fi
                fi;
            } pool;
    
        self;
    }};
};
