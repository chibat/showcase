var interval = 10000;

module ajaxPolling {

    class Data {
        id: number;
        // etc
    }

    class Vm {

        public requestCount = ko.observable<number>();
        public records = ko.observableArray<Data>();
        public visibleTable = ko.observable(false);
        public visibleDetail = ko.observable(false);
        public detail = ko.observable<Data>();

        public disp = () => {
            this.visibleTable(true);
            this.visibleDetail(false);
        }

        public dispDetail = (data: Data) => {
            this.detail(data);
            this.visibleTable(false);
            this.visibleDetail(true);
        }

        public startProgress = (data: Data) => {
            this.visibleTable(false);
            this.visibleDetail(false);

            $.ajax({
                url: "/ajaxPolling/startProgress.json",
                type: "POST",
                data: "id=" + data.id,
                async: false,
                success: (data: Data[]) => {
                    if (data.length > 0) {
                        this.requestCount(data.length);
                        this.records(data);
                    } else if (data.length === 0) {
                        this.requestCount(null);
                    }
                },
            });
        }

        public polling = () => {
            $.ajax({
                url: "/ajaxPolling/get.json",
                async: false,
                success: (data: Data[]) => {
                    if (data.length > 0) {
                        this.requestCount(data.length);
                        this.records(data);
                    } else if (data.length === 0) {
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

