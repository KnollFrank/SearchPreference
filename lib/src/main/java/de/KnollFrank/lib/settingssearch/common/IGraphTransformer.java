package de.KnollFrank.lib.settingssearch.common;

public interface IGraphTransformer<V1, E1, V2, E2> {

    V2 transformNode(V1 node);

    E2 transformEdge(E1 edge, V2 transformedParentNode);
}