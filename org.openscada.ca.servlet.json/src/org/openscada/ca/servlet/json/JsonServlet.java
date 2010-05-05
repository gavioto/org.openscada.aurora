package org.openscada.ca.servlet.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.DiffEntry;
import org.openscada.ca.Factory;
import org.openscada.ca.DiffEntry.Operation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JsonServlet extends HttpServlet
{
    private class FactorySerializer implements JsonSerializer<Factory>
    {
        public JsonElement serialize ( Factory factory, Type typeOfFactory, JsonSerializationContext context )
        {
            JsonObject obj = new JsonObject ();
            obj.addProperty ( "id", factory.getId () );
            obj.addProperty ( "description", factory.getDescription () );
            obj.addProperty ( "state", factory.getState ().toString () );
            return obj;
        }
    }

    private class DiffEntrySerializer implements JsonSerializer<DiffEntry>, JsonDeserializer<DiffEntry>
    {
        public JsonElement serialize ( DiffEntry config, Type typeOfDiffEntry, JsonSerializationContext context )
        {
            JsonObject obj = new JsonObject ();
            obj.addProperty ( "factoryId", config.getFactoryId () );
            obj.addProperty ( "configurationId", config.getConfigurationId () );
            obj.addProperty ( "operation", config.getOperation ().toString () );
            JsonObject map = new JsonObject ();
            for ( Entry<?, ?> entry : config.getData ().entrySet () )
            {
                map.addProperty ( String.valueOf ( entry.getKey () ), String.valueOf ( entry.getValue () ) );
            }
            obj.add ( "data", map );
            return obj;
        }

        public DiffEntry deserialize ( JsonElement element, Type type, JsonDeserializationContext context ) throws JsonParseException
        {
            final String factoryId = element.getAsJsonObject ().get ( "factoryId" ).getAsString ();
            final String configurationId = element.getAsJsonObject ().get ( "configurationId" ).getAsString ();
            final String operation = element.getAsJsonObject ().get ( "operation" ).getAsString ();
            final Map<String, String> data = context.deserialize ( element.getAsJsonObject ().getAsJsonObject ( "data" ), new TypeToken<Map<String, String>> () {}.getType () );
            return new DiffEntry ( factoryId, configurationId, Operation.valueOf ( operation ), data );
        }
    }

    private static final long serialVersionUID = -3311156226543946433L;

    private final ConfigurationAdministrator configurationAdmin;

    private final Gson gson = new GsonBuilder ().setPrettyPrinting ().registerTypeAdapter ( Factory.class, new FactorySerializer () ).registerTypeAdapter ( DiffEntry.class, new DiffEntrySerializer () ).create ();

    private final ObjectMapper mapper = new ObjectMapper ();

    public JsonServlet ( ConfigurationAdministrator configurationAdmin )
    {
        this.configurationAdmin = configurationAdmin;
    }

    @Override
    protected void doGet ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( req.getPathInfo () == null || "/".equals ( req.getPathInfo () ) )
        {
            redirectToFactoryList ( req, resp );
            return;
        }
        else if ( req.getPathInfo ().startsWith ( "/factory" ) )
        {
            getFactory ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/knownFactories" ) )
        {
            getKnownFactories ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/configurations" ) )
        {
            getConfigurations ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/configuration" ) )
        {
            getConfiguration ( req, resp );
        }
        else
        {
            send404Error ( req, resp );
        }
    }

    @Override
    protected void doPost ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( req.getPathInfo ().startsWith ( "/createConfiguration" ) )
        {
            createConfiguration ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/updateConfiguration" ) )
        {
            updateConfiguration ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/deleteConfiguration" ) )
        {
            deleteConfiguration ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/purgeFactory" ) )
        {
            purgeFactory ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/createDiff" ) )
        {
            createDiff ( req, resp );
        }
        else if ( req.getPathInfo ().startsWith ( "/applyDiff" ) )
        {
            applyDiff ( req, resp );
        }
        else
        {
            send404Error ( req, resp );
        }
    }

    private void defaultContentType ( HttpServletResponse resp )
    {
        resp.setContentType ( "text/javascript" );
    }

    private void send404Error ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        resp.sendError ( HttpServletResponse.SC_NOT_FOUND, "Not Found" );
    }

    private void redirectToFactoryList ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        resp.setHeader ( "Location", req.getServletPath () + "/knownFactories" );
        resp.sendError ( HttpServletResponse.SC_MOVED_PERMANENTLY, "Moved Permanently" );
    }

    private void getFactory ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        String factoryId = getRequiredString ( req, resp, "factory.id" );
        gson.toJson ( configurationAdmin.getFactory ( factoryId ), Factory.class, resp.getWriter () );
    }

    private void getKnownFactories ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        gson.toJson ( configurationAdmin.getKnownFactories (), Factory[].class, resp.getWriter () );
    }

    private void getConfigurations ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        String factoryId = getRequiredString ( req, resp, "factory.id" );
        gson.toJson ( configurationAdmin.getConfigurations ( factoryId ), resp.getWriter () );
    }

    private void getConfiguration ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        String factoryId = getRequiredString ( req, resp, "factory.id" );
        String id = getRequiredString ( req, resp, "id" );
        gson.toJson ( configurationAdmin.getConfiguration ( factoryId, id ), resp.getWriter () );
    }

    private void createConfiguration ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final String id = getRequiredString ( req, resp, "id" );
            final Map<String, String> properties = gson.fromJson ( req.getReader (), new TypeToken<Map<String, String>> () {}.getType () );
            final Future<Configuration> future = configurationAdmin.createConfiguration ( factoryId, id, properties );
            gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private void updateConfiguration ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final String id = getRequiredString ( req, resp, "id" );
            final String full = getOptionalString ( req, resp, "full", "" );
            final Map<String, String> properties = gson.fromJson ( req.getReader (), new TypeToken<Map<String, String>> () {}.getType () );
            final Future<Configuration> future = configurationAdmin.updateConfiguration ( factoryId, id, properties, "full".equals ( full ) );
            gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private void deleteConfiguration ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final String id = getRequiredString ( req, resp, "id" );
            final Future<Configuration> future = configurationAdmin.deleteConfiguration ( factoryId, id );
            gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private void purgeFactory ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            final String factoryId = getRequiredString ( req, resp, "factory.id" );
            final Future<Void> future = configurationAdmin.purgeFactory ( factoryId );
            gson.toJson ( future.get (), resp.getWriter () );
        }
        catch ( Exception e )
        {
            throw new ServletException ( e );
        }
    }

    @SuppressWarnings ( "unchecked" )
    private void createDiff ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );

        Map newConfiguration = mapper.readValue ( req.getReader (), HashMap.class );

        Map existingConfiguration = new HashMap ();
        // convert existing configuration to same format as new one
        for ( final Factory factory : configurationAdmin.getKnownFactories () )
        {
            Map config = new HashMap ();
            existingConfiguration.put ( factory.getId (), config );
            for ( Configuration configuration : configurationAdmin.getConfigurations ( factory.getId () ) )
            {
                config.put ( configuration.getId (), new HashMap ( configuration.getData () ) );
            }
        }

        // create diff
        List<DiffEntry> diff = new ArrayList<DiffEntry> ();

        // 1. find added
        for ( Entry<String, Map<String, Map<String, String>>> factory : ( (Map<String, Map<String, Map<String, String>>>)newConfiguration ).entrySet () )
        {
            String factoryId = factory.getKey ();
            Map<String, Map<String, String>> existingConfigs = (Map<String, Map<String, String>>)existingConfiguration.get ( factoryId );
            for ( final Entry<String, Map<String, String>> config : ( (Map<String, Map<String, String>>)newConfiguration.get ( factoryId ) ).entrySet () )
            {
                String id = config.getKey ();
                Map<String, String> data = config.getValue ();
                if ( existingConfigs == null )
                {
                    diff.add ( new DiffEntry ( factoryId, id, DiffEntry.Operation.ADD, new HashMap ( data ) ) );
                }
                else if ( !existingConfigs.containsKey ( id ) )
                {
                    diff.add ( new DiffEntry ( factoryId, id, DiffEntry.Operation.ADD, new HashMap ( data ) ) );
                }
                else if ( !data.equals ( existingConfigs.get ( id ) ) )
                {
                    diff.add ( new DiffEntry ( factoryId, id, DiffEntry.Operation.UPDATE, new HashMap ( data ) ) );
                }
            }
        }
        // find deleted
        for ( Entry<String, Map<String, Map<String, String>>> factory : ( (Map<String, Map<String, Map<String, String>>>)existingConfiguration ).entrySet () )
        {
            String factoryId = factory.getKey ();
            Map<String, Map<String, String>> newConfigs = (Map<String, Map<String, String>>)newConfiguration.get ( factoryId );
            for ( Entry<String, Map<String, String>> config : ( (Map<String, Map<String, String>>)existingConfiguration.get ( factoryId ) ).entrySet () )
            {
                String id = config.getKey ();
                if ( newConfigs == null )
                {
                    diff.add ( new DiffEntry ( factoryId, id, DiffEntry.Operation.DELETE, new HashMap () ) );
                }
                else if ( !newConfigs.containsKey ( id ) )
                {
                    diff.add ( new DiffEntry ( factoryId, id, DiffEntry.Operation.DELETE, new HashMap () ) );
                }
            }
        }
        gson.toJson ( diff, resp.getWriter () );
    }

    private void applyDiff ( HttpServletRequest req, HttpServletResponse resp ) throws IOException, ServletException
    {
        defaultContentType ( resp );
        try
        {
            Collection<DiffEntry> changeSet = gson.fromJson ( req.getReader (), new TypeToken<Collection<DiffEntry>> () {}.getType () );
            Future<Void> future = configurationAdmin.applyDiff ( changeSet );
            future.get ();
            resp.getWriter ().print ( changeSet.size () + " applied" );
        }
        catch ( Exception e )
        {
            throw new ServletException ( e );
        }
    }

    private String getRequiredString ( HttpServletRequest req, HttpServletResponse resp, String key ) throws ServletException, IOException
    {
        Object o = req.getParameterMap ().get ( key );
        if ( o == null )
        {
            resp.getWriter ().print ( "parameter " + key + " must not be null" );
            throw new ServletException ( "parameter " + key + " must not be null" );
        }
        return ( (String[])o )[0];
    }

    private String getOptionalString ( HttpServletRequest req, HttpServletResponse resp, String key, String def )
    {
        Object o = req.getParameterMap ().get ( key );
        if ( o == null )
        {
            return def;
        }
        return ( (String[])o )[0];
    }
}
