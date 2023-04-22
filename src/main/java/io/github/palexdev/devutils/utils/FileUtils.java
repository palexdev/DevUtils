package io.github.palexdev.devutils.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FileUtils {
    //================================================================================
    // Properties
    //================================================================================
    private static final DecimalFormat DEC_FORMAT = new DecimalFormat("#.##");

    //================================================================================
    // Constructors
    //================================================================================
    private FileUtils() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static String sizeToString(File file) {
        List<SizeUnit> units = SizeUnit.unitsInDescending();
        long size = file.length();

        if (size < 0)
            throw new IllegalArgumentException("Invalid file size: " + size);

        for (SizeUnit unit : units) {
            if (size >= unit.getUnitBase()) {
                return formatSize(size, unit.getUnitBase(), unit.name());
            }
        }
        return formatSize(size, SizeUnit.Bytes.getUnitBase(), SizeUnit.Bytes.name());
    }

    public static String getExtension(File file) {
        return Optional.ofNullable(file)
            .map(File::getName)
            .filter(n -> n.contains("."))
            .map(n -> n.substring(n.lastIndexOf(".") + 1))
            .orElse("");
    }

    private static String formatSize(long size, long divider, String unit) {
        return DEC_FORMAT.format((double) size / divider) + " " + unit;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public enum SizeUnit {
        Bytes(1L),
        KB(Bytes.unitBase * 1000),
        MB(KB.unitBase * 1000),
        GB(MB.unitBase * 1000),
        TB(GB.unitBase * 1000),
        PB(TB.unitBase * 1000),
        EB(PB.unitBase * 1000);

        private final Long unitBase;

        SizeUnit(Long unitBase) {
            this.unitBase = unitBase;
        }

        public Long getUnitBase() {
            return unitBase;
        }

        public static List<SizeUnit> unitsInDescending() {
            List<SizeUnit> list = Arrays.asList(values());
            Collections.reverse(list);
            return list;
        }
    }

}
