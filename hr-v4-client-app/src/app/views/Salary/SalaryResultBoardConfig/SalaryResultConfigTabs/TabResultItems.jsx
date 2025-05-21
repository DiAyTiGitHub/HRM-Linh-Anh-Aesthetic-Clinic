import { observer } from "mobx-react";
import { memo, useState } from "react";
import ResultItemTable from "./ResultItemsTable/ResultItemTable";

function TabTemplateItems() {

    return (
        <ResultItemTable />
    )
}

export default memo(observer(TabTemplateItems));
