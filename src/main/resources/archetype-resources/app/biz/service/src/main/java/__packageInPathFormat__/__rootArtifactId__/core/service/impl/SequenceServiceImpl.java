#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.core.service.impl;

import ${package}.${rootArtifactId}.core.service.SequenceService;
import ${package}.${rootArtifactId}.core.service.sequence.SequenceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sequenceService")
public class SequenceServiceImpl implements SequenceService {
    @Autowired
    private SequenceFactory sequenceFactory;

    @Override
    public String nextVal(String seqName) {
        return String.valueOf(sequenceFactory.getNextVal(seqName));
    }
}
