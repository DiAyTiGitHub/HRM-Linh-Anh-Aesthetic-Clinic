import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { makeStyles } from "@material-ui/core";
import { Delete } from "@material-ui/icons";
import DragIndicatorIcon from '@material-ui/icons/DragIndicator';
import { formatDate } from "app/LocalFunction";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "2px",
        overflowX: "auto",
        overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));


function StaticStaffRow(props) {
    const { t } = useTranslation();
    const classes = useStyles();

    const {
        index,
        remove,
        nameSpace,
        data
    } = props;

    const {
        attributes,
        listeners,
        transform,
        transition,
        setNodeRef,
        isDragging
    } = useSortable({
        id: data?.id
    });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition: transition,
        marginLeft: "0 !important",
        left: "0 !important"
    };

    const { setFieldValue, values } = useFormikContext();
    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (
        <>
            <table className={`${classes.table} w-100`} >
                <thead>
                    <tr
                        {...attributes} {...listeners}
                        className={`${classes.tableHeader} row-table-body bg-white`}
                        key={index}
                        style={style}
                        ref={setNodeRef}
                    >
                        <th width="128px" align="center">
                            <div className='w-100 one_row_display_cell aligns-center justify-center px-6'>
                                <span

                                    className="pr-8 pointer tooltip"
                                    style={{ cursor: 'grabbing' }}
                                >
                                    <DragIndicatorIcon className="text-green font-size-20" />
                                </span>

                                <span
                                    className="pointer tooltip"
                                    style={{ cursor: 'pointer' }}
                                >
                                    <Delete className="text-red font-size-20" />
                                </span>
                            </div>
                        </th>

                        <th width="15%">
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.staffCode || ""}
                            </span>
                        </th>

                        <th width="30%">
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.displayName || ""}
                            </span>
                        </th>

                        <th width="20%">
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {formatDate("DD/MM/YYYY", data?.birthDate)}
                            </span>
                        </th>

                        <th width="20%">
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data.gender === "M" ? "Nam" : data.gender === "F" ? "Nữ" : ""}
                            </span>
                        </th>
                    </tr>
                </thead>
            </table>
        </>
    );
}

export default memo(observer(StaticStaffRow));