import React from 'react';
import { useStore } from "../../stores";
import { observer } from 'mobx-react-lite';
import GlobitsTable from 'app/common/GlobitsTable';
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";

export default observer(function DisciplineList() {
    const { disciplineStore } = useStore();
    const { t } = useTranslation();
    const {
        disciplineList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleSelectListDiscipline,
        handleEditDiscipline,
        handleDeleteDiscipline,
    } = disciplineStore;
   const MaterialButton = (props) => {
        const { item } = props;
        return (
          <div>
            <IconButton size="small" onClick={() => {props.onSelect(item, 0); }}>
              <Icon fontSize="small" color="primary">
                edit
              </Icon>
            </IconButton>
            <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
              <Icon fontSize="small" color="secondary">
                delete
              </Icon>
            </IconButton>
          </div>
        );
      }
    const columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            ...Config.tableCellConfig,
            render: (rowData) => (
              <MaterialButton
                item={rowData}
                onSelect={(rowData, method) => {
                  if (method === 0) {
                    handleEditDiscipline(rowData.id);
                  } else if (method === 1) {
                    handleDeleteDiscipline(rowData.id);
                  } else {
                    alert("Call Selected Here:" + rowData.id);
                  }
                }}
              />
            ),
          },
        { title: t("discipline.code"),minWidth: "100px",  ...Config.tableCellConfig, field: "code" },
        { title: t("discipline.name"),minWidth: "100px", field: "name", ...Config.tableCellConfig },
        { title: t("discipline.languageKey"),minWidth: "100px", field: "languageKey",  ...Config.tableCellConfig },
         { title: t("discipline.disciplineType"),minWidth: "100px", field: "disciplineType", ...Config.tableCellConfig },
        { title: t("discipline.formal"),minWidth: "100px", field: "formal", ...Config.tableCellConfig },
        { title: t("discipline.evaluateYear"),minWidth: "100px", field: "evaluateYear",  ...Config.tableCellConfig },
        { title: t("discipline.description"),minWidth: "100px", field: "description",  ...Config.tableCellConfig },
      ]

    return (
        <GlobitsTable
        selection
        data={disciplineList}
        handleSelectList={handleSelectListDiscipline}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setRowsPerPage}
        pageSize={rowsPerPage}
        pageSizeOption={[10, 25, 50]}
        totalElements={totalElements}
        page={page}
      />
    )
})

