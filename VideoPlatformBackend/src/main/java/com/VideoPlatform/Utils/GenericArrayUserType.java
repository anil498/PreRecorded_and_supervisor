package com.VideoPlatform.Utils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

@Component
public class GenericArrayUserType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.ARRAY};
    }

    @Override
    public Class<Integer[]> returnedClass() {
        return Integer[].class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        if (o == o1) {
            return true;
        }
        if (o == null || o1 == null) {
            return false;
        }
        Integer[] arr1 = (Integer[]) o;
        Integer[] arr2 = (Integer[]) o1;
        return Objects.deepEquals(arr1, arr2);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return Objects.hashCode(o);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        return array != null ? array.getArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value != null && st != null) {
            Integer[] castObject = (Integer[]) value;
            Array array = session.connection().createArrayOf("integer", castObject);
            st.setArray(index, array);
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null) {
            return null;
        }
        Integer[] source = (Integer[]) o;
        Integer[] destination = new Integer[source.length];
        System.arraycopy(source, 0, destination, 0, source.length);
        return destination;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        if (o == null) {
            return null;
        }
        Integer[] array = (Integer[]) o;
        return array;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        if (serializable == null) {
            return null;
        }
        Integer[] array = (Integer[]) serializable;
        return array;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return deepCopy(o);
    }
}
 