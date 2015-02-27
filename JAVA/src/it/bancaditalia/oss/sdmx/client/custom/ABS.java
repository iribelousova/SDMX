/* Copyright 2010,2014 Bank Of Italy
*
* Licensed under the EUPL, Version 1.1 or - as soon they
* will be approved by the European Commission - subsequent
* versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the
* Licence.
* You may obtain a copy of the Licence at:
*
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in
* writing, software distributed under the Licence is
* distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied.
* See the Licence for the specific language governing
* permissions and limitations under the Licence.
*/
package it.bancaditalia.oss.sdmx.client.custom;

import it.bancaditalia.oss.sdmx.api.DataFlowStructure;
import it.bancaditalia.oss.sdmx.api.Dataflow;
import it.bancaditalia.oss.sdmx.api.PortableTimeSeries;
import it.bancaditalia.oss.sdmx.util.Configuration;
import it.bancaditalia.oss.sdmx.util.SdmxException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Attilio Mattiocco
 *
 */
public class ABS extends DotStat{
		
	protected static Logger logger = Configuration.getSdmxLogger();
	
	public ABS() throws MalformedURLException{
		super("ABS", new URL("http://stat.abs.gov.au/restsdmx/sdmx.ashx"), false);
	}
	
	@Override
	protected String buildDSDQuery(URL endpoint, String dsd, String agency, String version){
		if( endpoint!=null  &&
				dsd!=null && !dsd.isEmpty()){
			// agency=all is not working. we always use ABS
			String query = endpoint + "/GetDataStructure/" + dsd + "/ABS";
			return query;
		}
		else{
			throw new RuntimeException("Invalid query parameters: dsd=" + dsd + " endpoint=" + endpoint);
		}
	}
	
	@Override
	protected String buildDataQuery(URL endpoint, Dataflow dataflow, String resource, String startTime, String endTime){
		String query = super.buildDataQuery(endpoint, dataflow, resource + "/ABS", null, null);
		//query += "&format=compact_v2";
		if((startTime != null && !startTime.isEmpty()) || (endTime != null && !endTime.isEmpty())){
			query += "?";
			if(startTime != null){
				query=query+"&startTime="+startTime;
			}
			if(endTime != null){
				query=query+"&endTime="+endTime;
			}
		}
		return query;
	}

	@Override
	public List<PortableTimeSeries> getTimeSeries(Dataflow dataflow, DataFlowStructure dsd, String resource, String startTime, String endTime) throws SdmxException {
		return super.getTimeSeries(dataflow, dsd, fixWildcard(resource), startTime, endTime);
	}

	// https://github.com/amattioc/SDMX/issues/19
	private static String fixWildcard(String resource) {
		String[] items = resource.split("\\.");
		if (items.length <= 1) {
			return resource;
		}
		for (int i = 0; i < items.length; i++) {
			String tmp = items[i].trim();
			if (tmp.isEmpty() || tmp.equals("*")) {
				items[i] = "+";
			}
		}
		StringBuilder result = new StringBuilder();
		result.append(items[0]);
		for (int i = 1; i < items.length; i++) {
			result.append('.').append(items[i]);
		}
		return result.toString();
	}

}
