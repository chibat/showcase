var interval = 10000;

module ajaxPolling {

    class Record {
        id: number;
        // etc
    }

    class Vm {

        public requestCount = ko.observable<number>();
        public records = ko.observableArray<Record>();
        public visibleTable = ko.observable(false);
        public visibleDetail = ko.observable(false);
        public detail = ko.observable<Record>();

        public disp = () => {
            this.visibleTable(true);
            this.visibleDetail(false);
        }

        public dispDetail = (record: Record) => {
            this.detail(record);
            this.visibleTable(false);
            this.visibleDetail(true);
        }

        public startProgress = (record: Record) => {
            this.visibleTable(false);
            this.visibleDetail(false);

            $.ajax({
                url: "/ajaxPolling/startProgress.json",
                type: "POST",
                data: "id=" + record.id,
                async: false,
                success: (records: Record[]) => {
                    if (records.length > 0) {
                        this.requestCount(records.length);
                        this.records(records);
                    } else if (records.length === 0) {
                        this.requestCount(null);
                    }
                },
            });
        }

        public polling = () => {
            $.ajax({
                url: "/ajaxPolling/get.json",
                async: false,
                success: (records: Record[]) => {
                    if (records.length > 0) {
                        this.requestCount(records.length);
                        this.records(records);
                    } else if (records.length === 0) {
                        this.requestCount(null);
                    }
                },
                complete: () => {
                    setTimeout(this.polling, interval);
                }
            });
        }
    };

    var vm = new Vm();
    ko.applyBindings({ vm: vm });
    vm.polling();
}

