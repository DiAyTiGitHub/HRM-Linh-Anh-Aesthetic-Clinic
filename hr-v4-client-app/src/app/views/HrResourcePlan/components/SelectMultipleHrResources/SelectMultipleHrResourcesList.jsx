import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { Checkbox, Tooltip } from "@material-ui/core";
import { useFormikContext } from "formik";

function SelectMultipleHrResourcesList(props) {
    const { t } = useTranslation();
    const {} = props;
    const { hrResourcePlanStore } = useStore();
    const { values } = useFormikContext();

    const {
        hrResourcePlanList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListDelete,
        listOnDelete,
        listForSelect,
    } = hrResourcePlanStore;

    function handleSelectPosition(position) {
        let data = listOnDelete;

        const isSelected = data.some((item) => item?.id === position?.id);

        if (isSelected) {
            data = data.filter((item) => item.id !== position.id);
        } else {
            data = [...data, position];
        }

        handleSelectListDelete(data);
    }

    let columns = [
        {
            title: "Lựa chọn",
            sorting: false,
            align: "center",
            width: "10%",
            cellStyle: {
                textAlign: "center",
            },
            render: (rowData) => {
                // Check if the current staff is selected
                const isChecked = listOnDelete?.some((position) => position?.id === rowData?.id);
                console.log(isChecked);

                return (
                    <Tooltip title={isChecked ? "Bỏ chọn" : "Chọn"} placement='top'>
                        <Checkbox
                            className='pr-16'
                            id={`radio${rowData?.id}`}
                            name='radSelected'
                            value={rowData.id}
                            checked={isChecked}
                            onClick={(event) => handleSelectPosition(rowData)}
                        />
                    </Tooltip>
                );
            },
        },
        {
            title: t("Tên định biên"),
            field: "name",
            align: "left",
        },
        {
            title: t("Mã định biên"),
            field: "code",
            align: "left",
        },
        {
            title: t("Phòng ban"),
            field: "department.name",
            align: "left",
            render: (rowData) => <span className='pr-6'>{rowData?.department?.name}</span>,
        },
    ];

    return (
        <GlobitsTable
            // selection
            data={listForSelect}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(SelectMultipleHrResourcesList));
