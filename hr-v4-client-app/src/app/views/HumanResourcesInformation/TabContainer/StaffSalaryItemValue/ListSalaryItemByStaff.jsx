import { List, ListItem, ListItemText, makeStyles, TextField } from "@material-ui/core";
import { useStore } from "app/stores";
import clsx from "clsx";
import { observer } from "mobx-react";
import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";

// import StaffSalaryItemValueListTab from "./StaffSalaryItemValueListTab";

const useStyles = makeStyles((theme) => ({
    root: {
        "& .MuiAccordion-rounded": {
            borderRadius: "5px",
        },

        "& .MuiPaper-root": {
            borderRadius: "5px",
        },

        "& .MuiAccordionSummary-root": {
            borderRadius: "5px", // backgroundColor: "#EBF3F9",
            color: "#5899d1 ",
            fontWeight: "400",

            "& .MuiTypography-root": {
                fontSize: "1rem",
            },
        },

        "& .Mui-expanded": {
            "& .MuiAccordionSummary-root": {
                backgroundColor: "#EBF3F9",
                color: "#5899d1 ", // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
                fontWeight: "700",
                maxHeight: "50px !important",
                minHeight: "50px !important",
            },
            "& .MuiTypography-root": {
                fontWeight: 700,
            },
        },

        "& .MuiButton-root": {
            borderRadius: "0.125rem !important",
        },
    },
    listItem: {
        borderRadius: 4,
        paddingLeft: theme.spacing(2),
        paddingRight: theme.spacing(2),
        "&:hover": {
            backgroundColor: theme.palette.action.hover,
        },
    },
    selected: {
        backgroundColor: theme.palette.action.selected,
        "&:hover": {
            backgroundColor: theme.palette.action.selected,
        },
    },
}));

export default observer(function ListSalaryItemByStaff() {
    const classes = useStyles();
    const { t } = useTranslation();
    const { staffSalaryItemValueStore, staffStore, salaryItemStore } = useStore();
    const { selectedStaff } = staffStore;
    const { handleSelectedSalaryItem, getListByStaffId, listSalaryItem, selectedSalaryItem } = salaryItemStore;
    const { pagingStaffSalaryItemValue } = staffSalaryItemValueStore;
    const [searchText, setSearchText] = useState("");

    useEffect(() => {
        if (selectedStaff?.id) {
            getListByStaffId(selectedStaff?.id);
        }
    }, [selectedStaff?.id]);

    const filteredList = useMemo(() => {
        if (!searchText) return listSalaryItem;
        const lowerSearch = searchText.toLowerCase();
        return listSalaryItem?.filter((item) => item.name?.toLowerCase().includes(lowerSearch));
    }, [listSalaryItem, searchText]);

    return (
        <div style={{ height: "100%", overflow: "auto" }}>
            <div style={{ position: "sticky", top: 0, background: "#fff", zIndex: 1}}>
                <TextField
                    variant='outlined'
                    size='small'
                    placeholder={t("Tìm theo tên phần tử lương")}
                    fullWidth
                    value={searchText}
                    onChange={(e) => setSearchText(e.target.value)}
                    className={classes.searchBox}
                />
            </div>

            <List dense>
                {filteredList?.map((item) => (
                    <ListItem
                        key={item.id}
                        button
                        onClick={() => handleSelectedSalaryItem(item)}
                        className={clsx(classes.listItem, {
                            [classes.selected]: selectedSalaryItem?.id === item.id,
                        })}>
                        <ListItemText primary={item.name || "-"} />
                    </ListItem>
                ))}
            </List>
        </div>
    );
});
