package com.duowan.flowengine.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TopologyXmlParser {

	private File file;
	
	public TopologyXmlParser(File file) {
		super();
		this.file = file;
	}

	private static XStream buildXStreamer() {
		XStream xstream = new XStream(new DomDriver());
		xstream.useAttributeFor(int.class);
		xstream.useAttributeFor(long.class);
		xstream.useAttributeFor(char.class);
		xstream.useAttributeFor(float.class);
		xstream.useAttributeFor(boolean.class);
		xstream.useAttributeFor(double.class);
		xstream.useAttributeFor(Integer.class);
		xstream.useAttributeFor(Long.class);
		xstream.useAttributeFor(Character.class);
		xstream.useAttributeFor(Float.class);
		xstream.useAttributeFor(Double.class);
		xstream.useAttributeFor(Boolean.class);
		xstream.useAttributeFor(String.class);
		
		xstream.alias("topology", Topology.class);
		xstream.alias("topologyNode", TopologyNode.class);
		xstream.addImplicitCollection(Topology.class, "nodes");
		return xstream;
	}
	
	public Topology build() throws ClassNotFoundException, IOException {
		XStream x = buildXStreamer();
		FileInputStream input = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(input);
		try {
			Topology t = (Topology)x.fromXML(reader);
			t.init();
			return t;
		}finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	public static String toXml(Topology t) {
		XStream x = buildXStreamer();
		return x.toXML(t);
	}
}
