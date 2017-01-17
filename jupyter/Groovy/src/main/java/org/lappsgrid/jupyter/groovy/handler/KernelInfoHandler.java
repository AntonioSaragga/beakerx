package org.lappsgrid.jupyter.groovy.handler;

import org.lappsgrid.jupyter.groovy.GroovyKernel;
import org.lappsgrid.jupyter.groovy.msg.Header;
import org.lappsgrid.jupyter.groovy.msg.Message;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.lappsgrid.jupyter.groovy.msg.Message.Type.KERNEL_INFO_REPLY;

/**
 * Provides Jupyter with information about this kernel.
 * <p>
 * TODO: For some reason Jupyter always complains about timeouts
 * while waiting for the kernel_info_reply message...
 *
 * @author Keith Suderman
 */
public class KernelInfoHandler extends AbstractHandler {
    public KernelInfoHandler(GroovyKernel kernel) {
        super(kernel);
        logger = LoggerFactory.getLogger(KernelInfoHandler.class);
    }

    public void handle(Message message) throws NoSuchAlgorithmException {
        logger.info("Processing kernel info request");
        Message reply = new Message();
        LinkedHashMap<String, Serializable> map = new LinkedHashMap<String, Serializable>(6);
        map.put("protocol_version", "5.0");
        map.put("implementation", "groovy");
        map.put("implementation_version", "1.0.0");
        LinkedHashMap<String, Serializable> map1 = new LinkedHashMap<String, Serializable>(7);
        map1.put("name", "Groovy");
        map1.put("version", "2.4.6");
        map1.put("mimetype", "");
        map1.put("file_extension", ".groovy");
        map1.put("pygments_lexer", "");
        map1.put("codemirror_mode", "");
        map1.put("nbconverter_exporter", "");
        map.put("language_info", map1);
        map.put("banner", "Apache Groovy");
        map.put("help_links", new ArrayList());
        reply.setContent(map);
        reply.setHeader(new Header(KERNEL_INFO_REPLY, message.getHeader().getSession()));
        reply.setParentHeader(message.getHeader());
        reply.setIdentities(message.getIdentities());
        send(reply);
    }

}