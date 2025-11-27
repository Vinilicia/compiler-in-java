fn calculadora(op: char, x: int, y: float) -> float {
    if op == '+' {
        return x + y;
    }
    else if op == '-'{
        return x - y;
    }
    else if op == '*'{
        return x * y;
    }
    else if op == '/' {
        if y == 0 {
            return 0.0;
        }
        return x / y;
    }
    return 0.0;
}

fn main(){ 
    let a: int;
    let b: float;
    a = 1;
    b = 7;
    println("{}", calculadora('*', a, b));
}