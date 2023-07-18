package com.VideoPlatform.Utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Component
public class MapType implements UserType {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapType.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static int[] types = new int[] { Types.VARCHAR };
    private static JavaType javaType;

    public static JavaType getJavaType() {
        if (javaType == null) {
            synchronized (MapType.class) {
                javaType = MAPPER.getTypeFactory().constructType(new TypeReference<Map<String, Object>>() {});
                return javaType;
            }
        } else {
            return javaType;
        }
    }

    @Override
    public int[] sqlTypes() {
        return types;
    }

    @Override
    public Class<?> returnedClass() {
        return Map.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y)
            return true;
        if (null == x || null == y)
            return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String json = rs.getString(names[0]);
        try {
            if (json != null) {
                if (json.startsWith("[")) {
                    LOGGER.debug("Deserializing JSON array: {}", json);
                    List<Map<String, Object>> deserializedList = MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>(){});
                    LOGGER.debug("Deserialization result: {}", deserializedList);
                    return deserializedList;
                } else {
                    LOGGER.debug("Deserializing JSON object: {}", json);
                    Map<String, Object> deserializedMap = MAPPER.readValue(json, MapType.getJavaType());
                    LOGGER.debug("Deserialization result: {}", deserializedMap);
                    return deserializedMap;
                }
            }
        } catch (JsonParseException e) {
            LOGGER.error("Error parsing JSON string: {}", json);
            LOGGER.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            LOGGER.error("Error mapping JSON string: {}", json);
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error("IO error while parsing JSON string: {}", json);
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        String json = "";
        try {
            json = MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
        st.setString(index, json);
    }
}
