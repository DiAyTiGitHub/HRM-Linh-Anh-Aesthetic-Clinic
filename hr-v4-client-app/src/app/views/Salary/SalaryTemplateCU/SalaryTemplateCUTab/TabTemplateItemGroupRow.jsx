import { Icon , IconButton , Tooltip } from "@material-ui/core";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { observer } from "mobx-react";
import React , { memo , useState } from "react";
import { useTranslation } from 'react-i18next';
import { useStore } from "app/stores";

function TabTemplateItemGroupRow(props) {

    const {
        index ,
        templateItemGroups ,
        remove ,
        push ,
        nameSpace ,
        disabled ,
        readOnly
    } = props;

    const {hrRoleUtilsStore} = useStore();

    const {
        isAdmin ,
        isManager
    } = hrRoleUtilsStore;

    const {t} = useTranslation();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    const [openConfirmDeletePopup , setOpenConfirmDeletePopup] = useState(false);

    function handleConfirmDeleteItem() {
        // setOpenConfirmDeletePopup(true);
        remove();
    }

    return (<>
        <tr className='row-table-body' key={index}>
            {((isAdmin || isManager) && !readOnly) ? (
                <td align='center'>
                    <Tooltip placement="top" title="Xóa">
                        <IconButton
                            size='small'
                            style={{
                                color:'red'
                            }}
                            onClick={() => setOpenConfirmDeletePopup(true)}
                        >
                            <Icon fontSize='small'>
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </td>
            ) : (
                <td align='center'>
                    {index + 1}
                </td>
            )}

            <td>
                <GlobitsTextField
                    name={withNameSpace("name")}
                    readOnly={readOnly}
                />
            </td>

            <td>
                <GlobitsTextField
                    name={withNameSpace("description")}
                    readOnly={readOnly}
                />
            </td>
        </tr>

        {openConfirmDeletePopup && (<GlobitsConfirmationDialog
            open={openConfirmDeletePopup}
            onConfirmDialogClose={() => setOpenConfirmDeletePopup(false)}
            onYesClick={handleConfirmDeleteItem}
            title={t("confirm_dialog.delete.title")}
            text={"Bạn có chắc muốn xóa nhóm thành phần này?"}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
        />)}
    </>)
}

export default memo(observer(TabTemplateItemGroupRow));
