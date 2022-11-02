(* Think of these as abstract classes *)
class Comparator {
    compareTo(o1 : Object, o2 : Object) : Int {0};
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
