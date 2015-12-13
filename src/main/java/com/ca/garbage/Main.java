package com.ca.garbage;

import com.google.common.base.Stopwatch;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		benchmark(() -> FastNavIntSet.create(), "constructor fast", 1, TimeUnit.SECONDS, 3);
		benchmark(() -> new TreeSet<>(), "constructor treeset", 1, TimeUnit.SECONDS, 3);

		Set<Integer> set = new LinkedHashSet<>();
		int items = 100000;
		for (int i = 0; i < items; i++) {
			set.add(i);
		}
		FastNavIntSet fastNavSet = FastNavIntSet.fromCollection(set);
		NavigableSet<Integer> treeSet = new TreeSet<>(set);

		benchmark(() -> {
			for (int i = 0; i < items; i++) {
				fastNavSet.lower(i);
			}
		}, "lower fast", 1, TimeUnit.SECONDS, 10);
		benchmark(() -> {
			for (int i = 0; i < items; i++) {
				treeSet.lower(i);
			}
		}, "lower treeset", 1, TimeUnit.SECONDS, 10);
	}

	public static void benchmark(Runnable r, String name, long time, TimeUnit timeUnit, int numLoops) {
		for (int i = 0; i < numLoops; i++) {
			Stopwatch watch = Stopwatch.createStarted();
			long count = 0;
			while (watch.elapsed(timeUnit) < time) {
				r.run();
				++count;
			}
			System.out.println(name + ": " + count);
		}
	}
}
