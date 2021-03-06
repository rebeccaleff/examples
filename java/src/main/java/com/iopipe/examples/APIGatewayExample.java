package com.iopipe.examples;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.Context;
import com.iopipe.IOpipeExecution;
import com.iopipe.SimpleRequestHandlerWrapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.json.Json;
import javax.json.stream.JsonGenerator;

/**
 * This class provides an example of using API Gateway.
 *
 * @since 2018/05/15
 */
public class APIGatewayExample
	extends SimpleRequestHandlerWrapper<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
	/** Very interesting squirrel facts. */
	private static final String[] _SQUIRREL_FACTS =
		{
			"They are cute.",
			"They use geolocation to remember the location of their food stashes.",
			"Their hind feet can rotate 180 degrees for better grip running down trees.",
			"They are born with their eyes closed.",
			"Most have dichromatic vision and are red-green colorblind compared to humans.",
			"Many different species are found all around the world.",
			"They can run at their top speed up and down the trunks of trees.",
			"They do not hibernate in the winter, hence their need to stash acorns.",
			"They are omnivores and can and will eat what they can obtain.",
			"They love consuming junk food even though it is not good for their health.",
			"Their tails are long.",
			"They are curious, intelligent, and crafty; if they see food they will find a way to get it even if it takes a few tries.",
		};
	
	/**
	 * {@inheritDoc}
	 * @since 2018/05/15
	 */
	@Override
	protected APIGatewayProxyResponseEvent wrappedHandleRequest(IOpipeExecution __exec,
		APIGatewayProxyRequestEvent __val)
	{
		APIGatewayProxyResponseEvent rv = new APIGatewayProxyResponseEvent();
		
		try (StringWriter out = new StringWriter();
			JsonGenerator json = Json.createGenerator(out))
		{
			// Only accept squirrel facts when they want to be gotten
			Map<String, String> requests = __val.getQueryStringParameters();
			if (!"GET".equals(__val.getHttpMethod()) || requests == null ||
				!"squirrel".equals(requests.get("animal")))
			{
				rv.setStatusCode(400);
				
				json.writeStartObject();
				
				json.write("error", "Only GET requests with a query for the " +
					"key 'animal' containing the value 'squirrel' is valid. " +
					"Example: '?animal=squirrel'.");
				
				json.writeEnd();
			}
			
			// Is okay
			else
			{
				rv.setStatusCode(200);
				
				json.writeStartObject();
				
				json.write("fact", _SQUIRREL_FACTS[new Random().nextInt(
					_SQUIRREL_FACTS.length)]);
				
				json.writeEnd();
			}
			
			// Build JSON body
			json.flush();
			rv.setBody(out.toString());
		}
		
		// Oops!
		catch (IOException e)
		{
			rv.setStatusCode(500);
			rv.setBody("{\"error\": \"Internal IOException.\"}");
		}
		
		// Always uses JSON
		Map<String, String> headers = new LinkedHashMap<>();
		headers.put("Content-Type", "application/json");
		rv.setHeaders(headers);
		
		return rv;
	}
}

