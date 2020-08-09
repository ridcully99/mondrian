package org.ridcully.vragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * http://www.java2s.com/Open-Source/Android_Free_Code/Development/utility/net_comfreeze_libBundleBuilder_java.htm
 */

public class BundleBuilder {
    private Bundle data = new Bundle();

    public static BundleBuilder instance() {
        return new BundleBuilder();
    }

    public static BundleBuilder instance(Bundle data) {
        BundleBuilder builder = new BundleBuilder();
        if (null != data)
            builder.data = new Bundle(data);
        return builder;
    }

    public static BundleBuilder instance(ClassLoader loader) {
        BundleBuilder builder = new BundleBuilder();
        builder.data = new Bundle(loader);
        return builder;
    }

    public static BundleBuilder instance(int capacity) {
        BundleBuilder builder = new BundleBuilder();
        builder.data = new Bundle(capacity);
        return builder;
    }

    public Object get(String key) {
        if (data.containsKey(key))
            data.get(key);
        return null;
    }

    public BundleBuilder setArg(String key, Object value) {
        if (value instanceof Boolean)
            setArg(key, (Boolean) value);
        else if (value instanceof Boolean[])
            setArg(key, (boolean[]) value);
        else if (value instanceof Bundle)
            setArg(key, (Bundle) value);
        else if (value instanceof Byte)
            setArg(key, (Byte) value);
        else if (value instanceof Byte[])
            setArg(key, (byte[]) value);
        else if (value instanceof CharSequence)
            setArg(key, (CharSequence) value);
        else if (value instanceof CharSequence[])
            setArg(key, (CharSequence[]) value);
        else if (value instanceof Double)
            setArg(key, (Double) value);
        else if (value instanceof Double[])
            setArg(key, (double[]) value);
        else if (value instanceof Float)
            setArg(key, (Float) value);
        else if (value instanceof Float[])
            setArg(key, (float[]) value);
        else if (value instanceof Integer)
            setArg(key, (Integer) value);
        else if (value instanceof Integer[])
            setArg(key, (int[]) value);
        else if (value instanceof Long)
            setArg(key, (Long) value);
        else if (value instanceof Long[])
            setArg(key, (long[]) value);
        else if (value instanceof Parcelable)
            setArg(key, (Parcelable) value);
        else if (value instanceof Parcelable[])
            setArg(key, (Parcelable[]) value);
        else if (value instanceof Serializable)
            setArg(key, (Serializable) value);
        else if (value instanceof Short)
            setArg(key, (Short) value);
        else if (value instanceof Short[])
            setArg(key, (short[]) value);
        else if (value instanceof SparseArray)
            setArg(key, (SparseArray) value);
        else if (value instanceof String)
            setArg(key, (String) value);
        else if (value instanceof String[])
            setArg(key, (String[]) value);
        else if (value instanceof ArrayList)
            setArg(key, (ArrayList) value);
        return this;
    }

    public BundleBuilder setArg(String key, boolean value) {
        data.putBoolean(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, boolean[] value) {
        data.putBooleanArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, Bundle value) {
        data.putBundle(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, byte value) {
        data.putByte(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, byte[] value) {
        data.putByteArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, char value) {
        data.putChar(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, char[] value) {
        data.putCharArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, CharSequence value) {
        data.putCharSequence(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, CharSequence[] value) {
        data.putCharSequenceArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, double value) {
        data.putDouble(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, double[] value) {
        data.putDoubleArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, float value) {
        data.putFloat(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, float[] value) {
        data.putFloatArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, int value) {
        data.putInt(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, int[] value) {
        data.putIntArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, long value) {
        data.putLong(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, long[] value) {
        data.putLongArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, Parcelable value) {
        data.putParcelable(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, Parcelable[] value) {
        data.putParcelableArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, Serializable value) {
        data.putSerializable(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, short value) {
        data.putShort(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, short[] value) {
        data.putShortArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, SparseArray<Parcelable> value) {
        data.putSparseParcelableArray(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, String value) {
        data.putString(key, value);
        return this;
    }

    public BundleBuilder setArg(String key, String[] value) {
        data.putStringArray(key, value);
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public BundleBuilder setArg(String key, ArrayList value) {
        Object rawValue = value.get(0);
        if (rawValue instanceof CharSequence)
            data.putCharSequenceArrayList(key, value);
        else if (rawValue instanceof Integer)
            data.putIntegerArrayList(key, value);
        else if (rawValue instanceof Parcelable)
            data.putParcelableArrayList(key, value);
        else if (rawValue instanceof String)
            data.putStringArrayList(key, value);
        return this;
    }

    public Bundle build() {
        return this.data;
    }
}