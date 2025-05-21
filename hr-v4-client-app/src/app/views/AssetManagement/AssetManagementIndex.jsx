import React, { memo, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Tab } from "@material-ui/core";
import { TabContext, TabList, TabPanel } from '@material-ui/lab';
import { NavLink } from 'react-router-dom';
import ProductTypeIndex from './ProductType/ProductTypeIndex';
import ProductIndex from './Product/ProductIndex';

const LIST_ASSET = [
    {
        label: "Công cụ/ dụng cụ",
        path: "product",
        component: <ProductIndex />,
    },
    {
        label: "Loạicông cụ/ dụng cụ",
        path: "product-type",
        component: <ProductTypeIndex />,
    },
];

const AssetManagementIndex = () => {
    const { assetType } = useParams();
    const [tabIndex, setTabIndex] = useState("0");

    useEffect(() => {
        const tab = LIST_ASSET.findIndex(item => assetType === item.path);
        setTabIndex(tab >= 0 ? String(tab) : "0");
    }, [assetType]);

    return (
        <div>
            <TabContext value={tabIndex}>
                <TabList
                    orientation="horizontal"
                    onChange={(_, newTab) => setTabIndex(newTab)}
                    variant="scrollable"
                    scrollButtons="on"
                    indicatorColor="primary"
                    textColor="primary"
                >
                    {LIST_ASSET.map((item, index) => (
                        <Tab
                            key={index}
                            value={String(index)}
                            component={NavLink}
                            to={item.path}
                            label={item.label}
                        />
                    ))}
                </TabList>

                {LIST_ASSET.map((item, index) => (
                    <TabPanel className="p-0 cards-container mt-4" key={index} value={String(index)}>
                        {item.component}
                    </TabPanel>
                ))}
            </TabContext>
        </div>
    );
};

export default memo(AssetManagementIndex);
