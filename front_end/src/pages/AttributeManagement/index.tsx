interface Attribute {
    id: string;
    name: string;
    nameEn: string;
    description: string;
    descriptionEn: string;
    type: string;
    linkId: string;
    enable: boolean;
    constraint: {
        precision: number;
        length: number;
        notnull: boolean;
        variable: boolean;
        optionalValue: string;
        legalValueType: string;
        stockInDB: boolean;
        associationType: string;
        secretLevel: string;
        caseMode: string;
        compose: boolean;
        multiValue: boolean;
        encryption: boolean;
        index: boolean;
        graphIndex: boolean;
    };
    defaultValue: string | null;
    numberType: string | null;
    measuringUnitId: string | null;
    unitTypeId: string | null;
    enumNameEn: string | null;
    enumDefaultValueNameEn: string | null;
    extendedInformation: string | null;
} 