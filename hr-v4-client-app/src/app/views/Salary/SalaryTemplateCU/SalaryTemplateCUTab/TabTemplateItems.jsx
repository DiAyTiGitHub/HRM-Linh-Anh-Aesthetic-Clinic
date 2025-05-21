import { observer } from "mobx-react";
import { memo, useState } from "react";
import TemplateItemTableV2 from "./TemplateItemsTableV2/TemplateItemTableV2";

function TabTemplateItems() {

    return (
        <TemplateItemTableV2 />
    )
}

export default memo(observer(TabTemplateItems));
