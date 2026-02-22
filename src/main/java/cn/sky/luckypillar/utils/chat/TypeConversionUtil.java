package cn.sky.luckypillar.utils.chat;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class TypeConversionUtil {
	public static String StringList(List<String> strings) {
		final StringJoiner stringJoiner = new StringJoiner("\n");
        strings.forEach(stringJoiner::add);
		return stringJoiner.toString();
	}

	public static String StringList(String... strings) {
		final StringJoiner stringJoiner = new StringJoiner("\n");
        Arrays.asList(strings).forEach(stringJoiner::add);
		return stringJoiner.toString();
	}
}
