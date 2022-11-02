(* Think of these as abstract classes *)
class Comparator {
    compare_to(o1 : Object, o2 : Object) : Int { 0 };
};

class PriceComparator inherits Comparator {
    to_price(object : Object) : Int {
        case object of
            o : Object => { abort(); 0; };
            p : Product => p.getprice();
        esac
    };

    compare_to(o1 : Object, o2 : Object) : Int {
        let
            p1 : Int <- to_price(o1),
            p2 : Int <- to_price(o2)
        in
            if
                p1 = p2
            then
                0
            else
                if
                    p1 < p2
                then
                    ~1
                else
                    1
                fi
            fi
    };
};

class RankComparator inherits Comparator {
    to_rank_val(object : Object) : Int {
        case object of
            o : Object => { abort(); 0; };
            r : Rank => 1;
            p : Private => 2;
            c : Corporal => 3;
            s : Sergent => 4;
            o : Officer => 5;
        esac
    };

    compare_to(o1 : Object, o2 : Object) : Int {
        let
            r1 : Int <- to_rank_val(o1),
            r2 : Int <- to_rank_val(o2)
        in
            if
                r1 = r2
            then
                0
            else
                if
                    r1 < r2
                then
                    ~1
                else
                    1
                fi
            fi
    };
};

class AlphabeticComparator inherits Comparator {
    to_string(object : Object) : String {
        case object of
            o : Object => { abort(); ""; };
            s : String => s;
        esac
    };

    compare_to(o1 : Object, o2 : Object) : Int {
        let
            s1 : String <- to_string(o1),
            s2 : String <- to_string(o2)
        in
            if
                s1 = s2
            then
                0
            else
                if
                    s1 < s2
                then
                    ~1
                else
                    1
                fi
            fi
    };
};

class Filter {
    apply(object : Object) : Bool { { abort(); false; } };
};

class ProductFilter inherits Filter {
    apply(object: Object) : Bool {
        case object of
            p : Product => true;
            o : Object => false;
        esac
    };
};

class RankFilter inherits Filter {
    apply(object : Object) : Bool {
        case object of
            p : Rank => true;
            o : Object => false;
        esac
    };
};


class SamePriceFilter inherits Filter {
    apply(object : Object) : Bool {
        case object of
            p : Product => p.getprice() = p@Product.getprice();
            o : Object => false;
        esac
    };
};
