package org.reactome.server.service.controller.interactors;

import io.swagger.annotations.*;
import org.reactome.server.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.service.exception.ErrorInfo;
import org.reactome.server.service.manager.CustomInteractorManager;
import org.reactome.server.service.manager.InteractionManager;
import org.reactome.server.service.model.interactors.Interactors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */

@SuppressWarnings("unused")
@Api(tags = "interactors", description = "Molecule interactors")
@RequestMapping(value = "/interactors/token")
@RestController
public class TokenController {

    private static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");

    private static final String CUSTOM_RESOURCE_NAME = "custom";

    @Autowired
    public CustomInteractorManager customInteractionManager;

    @Autowired
    public InteractionManager interactionManager;

    @ApiOperation(value = "Retrieve custom interactions associated with a token", response = Interactors.class, produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 404, message = "Could not find the given token", response = ErrorInfo.class),
            @ApiResponse(code = 406, message = "Not acceptable according to the accept headers sent in the request", response = ErrorInfo.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
    })
    @RequestMapping(value = "/{token}", method = RequestMethod.POST, produces = "application/json", consumes = "text/plain")
    @ResponseBody
    public Interactors getInteractors(@ApiParam(value = "A token associated with a data submission", required = true)
                                      @PathVariable String token,
                                      @ApiParam(value = "Interactors accessions", required = true)
                                      @RequestBody String proteins) throws CustomPsicquicInteractionClusterException {
        infoLogger.info("Token {} query has been submitted", token);
        // Split param and put into a Set to avoid duplicates
        Set<String> accs = new HashSet<>(Arrays.asList(proteins.split("\\s*,\\s*")));
        Map<String, List<Interaction>> interactionMap = customInteractionManager.getInteractionsByTokenAndProteins(token, accs);
        return interactionManager.getCustomInteractionResult(interactionMap, CUSTOM_RESOURCE_NAME, token);
    }
}
