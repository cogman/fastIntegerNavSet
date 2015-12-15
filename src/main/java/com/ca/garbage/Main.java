package com.ca.garbage;

import com.google.common.base.Stopwatch;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		benchmark(() -> FastNavIntSet.create(), "constructor fast", 500, TimeUnit.MILLISECONDS, 3);
		benchmark(() -> new TreeSet<>(), "constructor treeset", 500, TimeUnit.MILLISECONDS, 3);

		Set<Integer> set = new LinkedHashSet<>();
		int items = 500;
		for (int i = 0; i < items; i++) {
			set.add(i);
		}
		NavigableSet<Integer> fastNavSet = FastNavIntSet.fromCollection(set);
		NavigableSet<Integer> treeSet = new TreeSet<>(set);
		Random rand = new Random();
		int[] randArray = rand.ints(0, items).limit(items).toArray();
		benchmark(() -> {
			for (int s : randArray) {
				fastNavSet.lower(s);
			}
		}, "lower fast", 1, TimeUnit.SECONDS, 400);
		benchmark(() -> {
			for (int s : randArray) {
				treeSet.lower(s);
			}
		}, "lower treeset", 1, TimeUnit.SECONDS, 4);
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
