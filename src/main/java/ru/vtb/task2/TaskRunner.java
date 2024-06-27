package ru.vtb.task2;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskRunner {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);

        List<Employee> employees = Arrays.asList(
                new Employee("Иванов Иван", 20, "Инженер"),
                new Employee("Петров Петр", 55, "Инструктор"),
                new Employee("Федоров Федор", 33, "Инженер"),
                new Employee("Светикова Светлана", 28, "Секретарь"),
                new Employee("Сидоров Иван", 18, "Стажер"),
                new Employee("Кузьмичев Кузьма", 39, "Инженер"),
                new Employee("Иванова Ольга", 60, "Бухгалтер"),
                new Employee("Олегов Олег", 41, "Инженер"),
                new Employee("Пупкин Василий", 22, "Инженер"));

        List<String> words = Arrays.asList("Слово", "Буква", "Длинное", "Семь", "Кот");

        String wordsString = "один два три один два четыре два два шесть четыре";

        String[] strings = {
                "один два три четыре пять",
                "слово буква длинное семь кот",
                "Иван Петр Федор Олег Яков",
                "кот собака мышь орел осел",
                "массив строка список мапа лист"};



        //Реализуйте удаление из листа всех дубликатов
        List<Integer> listWithoutDuplicates = list.stream().distinct().collect(Collectors.toList());

        //Найдите в списке целых чисел 3-е наибольшее число (пример: 5 2 10 9 4 3 10 1 13 => 10)
        int thirdMax = list.stream().sorted(Comparator.reverseOrder()).mapToInt(x -> x).limit(3).min().orElseThrow(() -> new RuntimeException("В списке меньше трех чисел"));

        //Найдите в списке целых чисел 3-е наибольшее «уникальное» число (пример: 5 2 10 9 4 3 10 1 13 => 9, в отличие от прошлой задачи здесь разные 10 считает за одно число)
        int uniqueThirdMax = list.stream().sorted(Comparator.reverseOrder()).mapToInt(x -> x).distinct().limit(3).min().orElseThrow(() -> new RuntimeException("В списке меньше трех уникальных чисел"));

        //Имеется список объектов типа Сотрудник (имя, возраст, должность), необходимо получить список имен 3 самых старших сотрудников с должностью «Инженер», в порядке убывания возраста
        List<String> names = employees.stream().filter(e -> e.position().equals("Инженер")).sorted((e1, e2) -> e2.age() - e1.age()).limit(3).map(e -> e.name()).collect(Collectors.toList());

        //Имеется список объектов типа Сотрудник (имя, возраст, должность), посчитайте средний возраст сотрудников с должностью «Инженер»
        double avgAge = employees.stream().filter(e -> e.position().equals("Инженер")).mapToInt(e -> e.age()).average().orElseThrow(() -> new RuntimeException("Пустой список"));

        //Найдите в списке слов самое длинное
        String longWord = words.stream().sorted((s1, s2) -> s2.length() - s1.length()).findFirst().orElseThrow(() -> new RuntimeException("Пустой список"));

        //Имеется строка с набором слов в нижнем регистре, разделенных пробелом. Постройте хеш-мапы, в которой будут хранится пары: слово - сколько раз оно встречается во входной строке
        Map<String, Long> wordMap = Arrays.asList(wordsString.split(" ")).stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        //Отпечатайте в консоль строки из списка в порядке увеличения длины слова, если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок
        words.stream().sorted((s1, s2) -> s1.length() == s2.length() ? s1.compareTo(s2) : s2.length() - s1.length()).forEach(System.out::println);

        //Имеется массив строк, в каждой из которых лежит набор из 5 строк, разделенных пробелом, найдите среди всех слов самое длинное, если таких слов несколько, получите любое из них
        String mostLongWord = Arrays.stream(strings).flatMap(s -> Arrays.stream(s.split(" "))).sorted((s1, s2) -> s2.length() - s1.length()).findFirst().orElseThrow(() -> new RuntimeException("Пустой список"));



        System.out.println(list);
        System.out.println(listWithoutDuplicates);
        System.out.println(thirdMax);
        System.out.println(uniqueThirdMax);
        System.out.println(names);
        System.out.println(avgAge);
        System.out.println(longWord);
        System.out.println(wordMap);
        System.out.println(mostLongWord);

    }


}
