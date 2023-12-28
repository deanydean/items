package items;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import items.authorizers.Authorizer;
import items.authorizers.KeyAuthorizer;
import items.store.DynamoDBStore;
import items.store.HeapStore;
import items.store.MapDBStore;
import items.store.MongoDBStore;
import items.store.Store;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.Spark;

/**
 * Start the IteMS services.
 */
public class Start 
{
    private static final Logger LOG = Logger.getLogger(Start.class.getName());
    
    public void startServices(String[] args) throws Exception
    {
        OptionSet options = parseArguments(args);
        
        String path = options.valueOf("path").toString();
        int port = (int) options.valueOf("port");
        
        Store store = null;
        if ( options.has("heap") )
        {
            store = new HeapStore();
        }
        else if ( options.has("mapdb"))
        {   
            store = new MapDBStore(
                options.valueOf("mapdb-file").toString(),
                options.valueOf("mapdb-map").toString()
            );
        }
        else if ( options.has("mongodb") )
        {
            store = new MongoDBStore(
                options.valueOf("mongodb-host").toString(),
                (int) options.valueOf("mongodb-port"),
                options.valueOf("mongodb-db").toString(),
                options.valueOf("mongodb-col").toString()
            );
        }
        else if ( options.has("dynamodb") )
        {
            store = new DynamoDBStore(
                options.valueOf("dynamodb-table").toString()
            );
        }
     
        Spark.port(port);
        
        if ( options.has("secure") )
        {
            // Enable security
            Spark.secure(
                options.valueOf("keystore").toString(), 
                new String(Files.readAllBytes(Paths.get(
                        options.valueOf("keystore-pwd-file").toString()))),
                options.valueOf("cert-alias").toString(), 
                options.valueOf("truststore").toString(),
                new String(Files.readAllBytes(Paths.get(
                        options.valueOf("truststore-pwd-file").toString()))),
                true);
        }

        Authorizer<String> authorizer = null;
        if (options.has("require-keys"))
        {
            var keys = Files.readAllLines(Paths.get("keys.txt"));
            authorizer = new KeyAuthorizer(keys);
        }
        
        new ItemsService(path, store, authorizer).start();
    }

    public static OptionSet parseArguments(String[] args)
    {
        OptionParser parser = new OptionParser();
        
        parser.recognizeAlternativeLongOptions(true);
        
        parser.accepts("path", "Path for the service")
            .withRequiredArg()
            .defaultsTo(ItemsService.SERVICE_BASE);
        
        parser.accepts("port", "Port to listen on")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(9999);
        
        parser.accepts("secure", "Enable secure mode");
        
        parser.accepts("keystore", "Java keystore")
            .withRequiredArg()
            .defaultsTo("./etc/keystore");
        
        parser.accepts("keystore-pwd-file", "File containing keystore pwd")
            .withRequiredArg()
            .defaultsTo("./etc/keystore-pwd");
        
        parser.accepts("cert-alias", "The alias of the server cert")
            .withRequiredArg()
            .defaultsTo("items-service-cert");
        
        parser.accepts("truststore", "Java keystore containing trusted certs")
            .withRequiredArg()
            .defaultsTo("./etc/keystore");
        
        parser.accepts("truststore-pwd-file", "File containing truststore pwd")
            .withRequiredArg()
            .defaultsTo("./etc/keystore-pwd");
        
        parser.accepts("heap", "Hold data on the heap");
        
        parser.accepts("mapdb", "Hold data in mapdb");
        
        parser.accepts("mapdb-file", "File to hold the mapdb")
            .requiredIf("mapdb")
            .withRequiredArg();
        
        parser.accepts("mapdb-map", "mapdb map name")
            .requiredIf("mapdb")
            .withRequiredArg();
        
        parser.accepts("mongodb", "Hold data in mongodb");
        
        parser.accepts("mongodb-host", "Hostname of the mongodb server")
            .requiredIf("mongodb")
            .withRequiredArg();
        
        parser.accepts("mongodb-port", "Port to connect to mongodb")
            .requiredIf("mongodb")
            .withRequiredArg().ofType(Integer.class);
        
        parser.accepts("mongodb-db", "mongodb database")
            .requiredIf("mongodb")
            .withRequiredArg();
        
        parser.accepts("mongodb-col", "mongodb collection")
            .requiredIf("mongodb")
            .withRequiredArg();

        parser.accepts("dynamodb", "Hold data in AWS DynamoDB");

        parser.accepts("dynamodb-table", "DynamoDB table name")
              .requiredIf("dynamodb")
              .withRequiredArg();

        parser.accepts("require-keys", "Require API keys for all incoming request");
        
        return parser.parse(args);
    }
    
    public static void main(String[] args)
    {
        try
        {
            new Start().startServices(args);
        }
        catch ( Throwable t )
        {
            LOG.log(Level.SEVERE, "Failed to start items : {0}", t);
        }
    }
    
}
