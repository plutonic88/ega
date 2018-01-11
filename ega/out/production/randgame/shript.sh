



for i in {0..49}; do java -jar gamut.jar -g RandomGame -players 2 -normalize -min_payoff 0 -max_payoff 100 -f $i.gamut -actions $1 $1; done

