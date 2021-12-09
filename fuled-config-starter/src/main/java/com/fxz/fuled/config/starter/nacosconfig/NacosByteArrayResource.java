package com.fxz.fuled.config.starter.nacosconfig;

import org.springframework.core.io.ByteArrayResource;

public class NacosByteArrayResource  extends ByteArrayResource {

    private String filename;

    /**
     * Create a new {@code ByteArrayResource}.
     * @param byteArray the byte array to wrap
     */
    public NacosByteArrayResource(byte[] byteArray) {
        super(byteArray);
    }

    /**
     * Create a new {@code ByteArrayResource} with a description.
     * @param byteArray the byte array to wrap
     * @param description where the byte array comes from
     */
    public NacosByteArrayResource(byte[] byteArray, String description) {
        super(byteArray, description);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * This implementation always returns {@code null}, assuming that this resource type
     * does not have a filename.
     */
    @Override
    public String getFilename() {
        return null == this.filename ? this.getDescription() : this.filename;
    }

}
