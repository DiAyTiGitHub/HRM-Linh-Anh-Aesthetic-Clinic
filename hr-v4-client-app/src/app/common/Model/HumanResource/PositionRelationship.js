import LocalConstants from "app/LocalConstants";

export class PositionRelationship {
    id = null;
    positionId = null;
    supervisor = null;
    relationshipType = null;

    constructor() {
        this.relationshipType = LocalConstants.PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.value;
    }
}